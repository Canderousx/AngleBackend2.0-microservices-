package com.reportService.app.Services.Report.Interfaces;

import com.reportService.app.Config.Exceptions.ReportNotFoundException;
import com.reportService.app.Config.Exceptions.SolutionDoesNotExistException;
import com.reportService.app.Config.Exceptions.TypeDoesNotExistException;
import com.reportService.app.Models.Records.ReportSolution;
import com.reportService.app.Models.Report;

public interface ReportManagementInterface {
    void addReport(Report report) throws TypeDoesNotExistException;
    void removeReport(String id);
    void solveReport(ReportSolution solution) throws ReportNotFoundException, SolutionDoesNotExistException;
    void reopenReport(String reportId) throws ReportNotFoundException;
}
