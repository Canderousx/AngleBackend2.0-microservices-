package com.videoManager.app.Models.Records;

import com.videoManager.app.Models.Projections.VideoProjection;

import java.util.Date;

public record VideoRecord(
        String id,
        String authorId,
        String name,
        String description,
        String thumbnail,
        String playlistName,
        Date datePublished,
        Long views,
        boolean processing
) implements VideoProjection {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAuthorId() {
        return authorId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String getPlaylistName() {
        return playlistName;
    }

    @Override
    public Date getDatePublished() {
        return datePublished;
    }

    @Override
    public Long getViews() {
        return views;
    }

    @Override
    public boolean getProcessing() {
        return processing;
    }
}
