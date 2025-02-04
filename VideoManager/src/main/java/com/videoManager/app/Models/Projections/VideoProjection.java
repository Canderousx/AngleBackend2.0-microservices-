package com.videoManager.app.Models.Projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.videoManager.app.Models.Records.VideoRecord;

import java.util.Date;

@JsonDeserialize(as = VideoRecord.class)
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
    boolean getIsBanned();
}
