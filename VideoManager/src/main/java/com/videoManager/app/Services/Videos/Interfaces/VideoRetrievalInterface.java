package com.videoManager.app.Services.Videos.Interfaces;

import com.videoManager.app.Config.Exceptions.MediaBannedException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Services.Cache.PageWrapper;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface VideoRetrievalInterface {

    PageWrapper<VideoProjection> getUserVideos(String userId, int page, int pageSize);

    PageWrapper<VideoProjection> getCurrentUserVideos(int page, int pageSize);

    String getAuthorId(String videoId);

    Video getRawVideo(String videoId) throws MediaNotFoundException, IOException, ClassNotFoundException, MediaBannedException;

    VideoProjection getVideo(String videoId) throws MediaNotFoundException, MediaBannedException;

    PageWrapper<VideoProjection>getLatestVideos(int page, int pageSize);

    Path getStreamPath(String videoId);

    List<VideoProjection>getMostPopular(int quantity);

    PageWrapper<VideoProjection> getBySubscribers(int page,int pageSize) throws BadRequestException, InterruptedException;

    List<VideoProjection> getSimilar(String videoId) throws MediaNotFoundException;

    int howManyUserVideos(String userId);



}
