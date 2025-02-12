package com.commentsManager.app.Models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Data
public class Comment {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "authorid", columnDefinition = "VARCHAR(36)")
    private String authorId;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "videoid", columnDefinition = "VARCHAR(36)")
    private String videoId;

    @Nullable
    private String parentCommentId;

    @Nullable
    private String parentCommentAuthorId;

    private Date datePublished;

    private String authorName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "isbanned")
    private boolean isBanned = false;

}
