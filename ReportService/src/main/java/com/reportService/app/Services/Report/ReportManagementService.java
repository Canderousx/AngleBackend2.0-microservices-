package com.reportService.app.Services.Report;


import com.reportService.app.Config.Exceptions.ReportNotFoundException;
import com.reportService.app.Config.Exceptions.SolutionDoesNotExistException;
import com.reportService.app.Config.Exceptions.TypeDoesNotExistException;
import com.reportService.app.Models.Records.BanData;
import com.reportService.app.Models.Records.ReportSolution;
import com.reportService.app.Models.Report;
import com.reportService.app.Models.ReportCategories;
import com.reportService.app.Models.ReportSolutions;
import com.reportService.app.Models.ReportTypes;
import com.reportService.app.Repositories.ReportRepository;
import com.reportService.app.Services.API.Interfaces.AuthServiceAPI;
import com.reportService.app.Services.JsonUtils;
import com.reportService.app.Services.Kafka.KafkaSenderService;
import com.reportService.app.Services.Report.Interfaces.ReportManagementInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReportManagementService implements ReportManagementInterface {

    private final ReportRetrievalService retrievalService;

    private final ReportRepository reportRepository;

    private final KafkaSenderService kafkaSenderService;

    private final AuthServiceAPI authServiceAPI;


    private BanData getBanData(Report report){
        return new BanData(
                report.getId(),
                report.getReportedAccountId(),
                report.getReporterId(),
                report.getMediaId(),
                report.getReason(),
                report.getDateResolved()
        );
    }



    private void banEvent(BanData banData, String topic){
        String json = JsonUtils.toJson(banData);
        kafkaSenderService.send(topic+"_banned",json);
    }

    private void unbanEvent(BanData banData,String topic){
        String json = JsonUtils.toJson(banData);
        kafkaSenderService.send(topic+"_unbanned",json);
    }

    @Override
    public void addReport(Report report) throws TypeDoesNotExistException {
        if(!ReportTypes.checkIfExists(report.getType())){
            throw new TypeDoesNotExistException("Report type {"+report.getType()+"} is not supported. Please USE: "+ReportTypes.readAll());
        }
        if(!ReportCategories.checkIfExist(report.getCategory())){
            throw new TypeDoesNotExistException("Report category {"+report.getCategory()+"} is not supported. Please USE: "+ReportCategories.readAll());
        }
        report.setReporterId(SecurityContextHolder.getContext().getAuthentication().getName());
        report.setDatePublished(new Date());
        report.setReporterName(authServiceAPI.getUsername(report.getReporterId()));
        report.setReportedName(authServiceAPI.getUsername(report.getReportedAccountId()));
        reportRepository.save(report);
        String json = JsonUtils.toJson(report);
        kafkaSenderService.send("new_report",json);
    }

    @Override
    public void removeReport(String id) {
        reportRepository.deleteById(id);
    }

    private void handleReportSolvedAlready(Report report, String newSolution){
        String prevSolution = report.getSolution();
        if(newSolution.equals(ReportSolutions.CANCELED.name()) && !newSolution.equals(prevSolution)){
            String type = prevSolution.equals(ReportSolutions.ACCOUNT_BANNED.name()) ? "account" : report.getType();
            unbanEvent(getBanData(report),type);
        }
        if (newSolution.equals(ReportSolutions.MEDIA_BANNED.name()) && prevSolution.equals(ReportSolutions.ACCOUNT_BANNED.name())){
            String type = "account";
            unbanEvent(getBanData(report),type);
        }

    }

    @Override
    @Transactional
    @Modifying
    public void solveReport(ReportSolution solution) throws ReportNotFoundException, SolutionDoesNotExistException {
        if(!ReportSolutions.checkIfExist(solution.solution())){
            throw new SolutionDoesNotExistException("Solution {"+solution+"} is not supported. Please USE: "+ReportSolutions.readAll());
        }
        Report toSolve = retrievalService.getReport(solution.reportId());

        boolean solvedAlready = toSolve.isResolved();
        if(solvedAlready){
            handleReportSolvedAlready(toSolve,solution.solution());
        }
        toSolve.setSolution(solution.solution());
        toSolve.setReason(solution.reason());
        toSolve.setDateResolved(new Date());
        toSolve.setResolvedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        toSolve.setResolved(true);
        reportRepository.save(toSolve);

        if(!toSolve.getSolution().equalsIgnoreCase(ReportSolutions.CANCELED.name())){
            BanData banData = getBanData(toSolve);
            String type = toSolve.getSolution().equals(ReportSolutions.ACCOUNT_BANNED.name()) ? "account" : toSolve.getType();
            banEvent(banData,type);
        }

    }

    @Override
    @Transactional
    @Modifying
    public void reopenReport(String reportId) throws ReportNotFoundException {
        Report toOpen = retrievalService.getReport(reportId);
        if(toOpen.isResolved()){
            String prevSolution = toOpen.getSolution();
            String prevType = toOpen.getType();
            BanData banData = getBanData(toOpen);
            toOpen.setResolved(false);
            toOpen.setResolvedBy(null);
            toOpen.setDateResolved(null);
            toOpen.setReason(null);
            toOpen.setSolution(null);
            reportRepository.save(toOpen);
            if(!prevSolution.equalsIgnoreCase(ReportSolutions.CANCELED.name())){
                if(prevSolution.equalsIgnoreCase(ReportSolutions.ACCOUNT_BANNED.name())){
                    unbanEvent(banData,"account");
                } else if (prevType.equalsIgnoreCase(ReportTypes.VIDEO.name())) {
                    unbanEvent(banData,"video");
                } else if (prevType.equalsIgnoreCase(ReportTypes.COMMENT.name())) {
                    unbanEvent(banData,"comment");
                }
            }
        }
    }
}
