package com.reportService.app.Models;


import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Report {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private String reporterId;

    private String reporterName;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private String reportedAccountId;

    private String reportedName;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private String mediaId;

    private String type;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Date datePublished;

    private boolean resolved;

    @Nullable
    private String solution;

    @Nullable
    @Column(columnDefinition = "TEXT")
    private String reason;

    @Nullable
    private String resolvedBy;

    @Nullable
    private Date dateResolved;



}
