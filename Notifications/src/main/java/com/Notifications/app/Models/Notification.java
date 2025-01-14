package com.Notifications.app.Models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Data
public class Notification {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    private String ownerId;

    private Date datePublished;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Nullable
    private String image;

    @Nullable
    private String url;

    private boolean forUser = false;

    private boolean seen = false;
}
