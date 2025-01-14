package com.videoManager.app.Models.Projections;

import java.util.Date;

public interface VideoProjection {
    String getId();
    String getAuthorId();
    
    String getName();
    String getDescription();
    String getThumbnail();

    String getPlaylistName();
    Date getDatePublished();
    long getViews();
    boolean getProcessing();
}
