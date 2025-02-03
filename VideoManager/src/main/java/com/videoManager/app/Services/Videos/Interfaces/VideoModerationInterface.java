package com.videoManager.app.Services.Videos.Interfaces;


import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Models.Records.VideoDetails;

import java.util.Map;

public interface VideoModerationInterface {
    void removeVideo(String id) throws MediaNotFoundException, FileServiceException, UnauthorizedException;

    void banVideo(String videoId) throws MediaNotFoundException;
    void banUserVideos(String userId);
    void unbanUserVideos(String userId);
    void unbanVideo(String videoId) throws MediaNotFoundException;

    void setMetadata(String id, VideoDetails metadata) throws MediaNotFoundException, UnauthorizedException;

    void updateViews(Map<String,Long> data);

    void setThumbnail(String id, String tbUrl) throws MediaNotFoundException;


}
