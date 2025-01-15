package com.reportService.app.Services.Report.Interfaces;

import com.reportService.app.Models.Report;

public interface ReportManagementInterface {
    void addReport(Report report);
    void removeReport(String id);
    void solveReport(String reportId,String solution);
}
