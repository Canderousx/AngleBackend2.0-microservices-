package com.videoManager.app.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Getter
@Setter
public class Video {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    private String name;

    @Column(name = "datepublished")
    private Date datePublished;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "video_tags",
            joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags;

    @Column(columnDefinition = "TEXT")
    private String description;

    private long views = 0;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "authorid", columnDefinition = "VARCHAR(36)")
    private String authorId;

    private String rawPath;

    private String playlistName;

    private String thumbnail;

    private boolean isPrivate = false;

    @Column(name = "isbanned")
    private boolean isBanned = false;

    private boolean processing = false;


}
