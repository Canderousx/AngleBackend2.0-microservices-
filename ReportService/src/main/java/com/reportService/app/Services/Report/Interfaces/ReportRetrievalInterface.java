package com.reportService.app.Services.Report.Interfaces;

import com.reportService.app.Config.Exceptions.ReportNotFoundException;
import com.reportService.app.Models.Report;
import org.springframework.data.domain.Page;

public interface ReportRetrievalInterface {
    long countUnresolved();
    long countUnresolved(String category);
    long countResolved();
    long countResolved(String adminId);
    long countResolved(String adminId,String category);
    Report getReport(String id) throws ReportNotFoundException;
    Page<Report>getByReportedId(String reportedId,int page, int pageSize);
    Page<Report>getByReportedId(String reportedId,String solution, int page, int pageSize);
    Page<Report>getByCategory(String category,int page, int pageSize);
    Page<Report>getByCategory(String category, boolean resolved, int page, int pageSize);
    Page<Report>getByCategory(String category,String adminId,int page, int pageSize);
    Page<Report>getUnresolved(int page, int pageSize);
    Page<Report>getUnresolved(String category, int page, int pageSize);
    Page<Report>getResolved(int page, int pageSize);
    Page<Report>getResolved(String adminId, int page, int pageSize);
    Page<Report>getResolved(String adminId,String category,int page, int pageSize);
}
