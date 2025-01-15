package com.videoManager.app.Services.Videos.Interfaces;


import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Models.Video;

public interface VideoModerationInterface {

    void registerView(String videoId) throws MediaNotFoundException;

    void removeVideo(String id) throws MediaNotFoundException, FileServiceException, UnauthorizedException;

    void banVideo(String videoId) throws MediaNotFoundException;
    void banUserVideos(String userId);
    void unbanUserVideos(String userId);
    void unbanVideo(String videoId) throws MediaNotFoundException;

    void dislikeVideo(String videoId) throws MediaNotFoundException;

    void likeVideo(String videoId) throws MediaNotFoundException;

    void removeRating(String videoId);

    void setMetadata(String id, VideoDetails metadata) throws MediaNotFoundException, UnauthorizedException;

    void setThumbnail(String id, String tbUrl) throws MediaNotFoundException;


}
