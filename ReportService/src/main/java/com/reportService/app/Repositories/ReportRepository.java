package com.reportService.app.Repositories;

import com.reportService.app.Models.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    Page<Report>findByReporterId(String reporterId,Pageable pageable);
    Page<Report>findByReportedAccountId(String reportedAccountId,Pageable pageable);
    Page<Report>findBySolution(String solution,Pageable pageable);
    Page<Report>findBySolutionAndReportedAccountId(String solution, String reportedAccountId,Pageable pageable);
    Page<Report>findBySolutionAndReporterId(String solution, String reporterId,Pageable pageable);
    Page<Report>findByCategory(String category,Pageable pageable);
    Page<Report>findByCategoryAndResolvedFalse(String category, Pageable pageable);
    Page<Report>findByCategoryAndResolvedTrue(String category,Pageable pageable);
    Page<Report>findByResolvedFalse(Pageable pageable);
    Page<Report>findByResolvedTrue(Pageable pageable);
    Page<Report>findByResolvedBy(String resolvedBy,Pageable pageable);
    Page<Report>findByResolvedByAndCategory(String resolvedBy, String category,Pageable pageable);
    long countByResolvedFalse();
    long countByCategoryAndResolvedFalse(String category);
    long countByResolvedTrue();
    long countByCategoryAndResolvedTrue(String category);
    long countByResolvedBy(String resolvedBy);
    long countByCategoryAndResolvedBy(String category, String resolvedBy);

}
