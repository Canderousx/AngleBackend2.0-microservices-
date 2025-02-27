package com.reportService.app.Services.Report;

import com.reportService.app.Config.Exceptions.ReportNotFoundException;
import com.reportService.app.Models.Report;
import com.reportService.app.Repositories.ReportRepository;
import com.reportService.app.Services.Report.Interfaces.ReportRetrievalInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportRetrievalService implements ReportRetrievalInterface {

    private final ReportRepository reportRepository;

    private Pageable getPageable(int page, int pageSize){
        return PageRequest.of(page,pageSize);
    }

    private Pageable getPageable(int page, int pageSize, Sort sort){
        return PageRequest.of(page,pageSize,sort);
    }

    private Sort sortByDatePublished(boolean ascending){
        if(ascending){
            return Sort.by("datePublished").ascending();
        }
        return Sort.by("datePublished").descending();
    }

    private Sort sortByDateResolved(boolean ascending){
        if(ascending){
            return Sort.by("dateResolved").ascending();
        }
        return Sort.by("dateResolved").descending();
    }

    private Sort sortBy(String sortBy,boolean ascending){
        if(ascending){
            return Sort.by(sortBy).ascending();
        }
        return Sort.by(sortBy).descending();
    }

    @Override
    public long countUnresolved() {
        return reportRepository.countByResolvedFalse();
    }

    @Override
    public long countUnresolved(String category) {
        return reportRepository.countByCategoryAndResolvedFalse(category);
    }

    @Override
    public long countResolved() {
        return reportRepository.countByResolvedTrue();
    }

    @Override
    public long countResolved(String adminId) {
        return reportRepository.countByResolvedBy(adminId);
    }

    @Override
    public long countResolved(String adminId, String category) {
        return reportRepository.countByCategoryAndResolvedBy(category,adminId);
    }

    @Override
    public Report getReport(String id) throws ReportNotFoundException {
        Report report = reportRepository.findById(id).orElse(null);
        if(report == null){
            throw new ReportNotFoundException("Report "+id+" not found");
        }
        return report;
    }

    @Override
    public Page<Report> getByReportedId(String reportedId, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByReportedAccountId(reportedId,getPageable(page,pageSize,sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getByReportedId(String reportedId, String solution, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findBySolutionAndReportedAccountId(solution,reportedId,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getByCategory(String category, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByCategory(category,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getByCategory(String category, boolean resolved, int page, int pageSize,boolean ascending,String sortBy) {
        if(resolved){
            return reportRepository.findByCategoryAndResolvedTrue(category,getPageable(page,pageSize, sortBy(sortBy,ascending)));
        }
        return reportRepository.findByCategoryAndResolvedFalse(category,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getByCategory(String category, String adminId, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByCategoryAndResolvedByAndResolvedTrue(category,adminId,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getUnresolved(int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByResolvedFalse(getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getUnresolved(String category, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByCategoryAndResolvedFalse(category,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getResolved(int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByResolvedTrue(getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getResolved(String adminId, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByResolvedBy(adminId,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }

    @Override
    public Page<Report> getResolved(String adminId, String category, int page, int pageSize,boolean ascending,String sortBy) {
        return reportRepository.findByResolvedByAndCategory(adminId,category,getPageable(page,pageSize, sortBy(sortBy,ascending)));
    }
}
