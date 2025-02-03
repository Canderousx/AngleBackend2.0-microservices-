package com.videoManager.app.Models.Projections;

import java.util.Date;

public interface VideoProjection {
    String getId();
    String getAuthorId();
    Long getViews();
    String getName();
    String getDescription();
    String getThumbnail();

    String getPlaylistName();
    Date getDatePublished();
    boolean getProcessing();
}
