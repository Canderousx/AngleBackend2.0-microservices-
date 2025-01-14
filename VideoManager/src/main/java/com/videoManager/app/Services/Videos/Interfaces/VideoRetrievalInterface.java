package com.videoManager.app.Services.Videos.Interfaces;

import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Models.Records.VideoLikesNDislikes;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface VideoRetrievalInterface {

    Page<VideoRecord> getAllVideos(int page,int pageSize);

    Page<VideoRecord> getUserVideos(String userId, int page, int pageSize);

    String getAuthorId(String videoId);

    Video getRawVideo(String videoId) throws MediaNotFoundException, IOException, ClassNotFoundException;

    VideoRecord getVideo(String videoId) throws MediaNotFoundException;

    VideoLikesNDislikes getVideoLikesNDislikes(String videoId);

    Page<VideoRecord>getLatestVideos(int page, int pageSize);

    Path getStreamPath(String videoId);

    long countLikes(String videoId);

    long countDislikes(String videoId);

    List<VideoRecord>getMostPopular();

    Page<VideoRecord> getBySubscribers(int page,int pageSize, String token) throws BadRequestException, InterruptedException;

    List<VideoRecord> getSimilar(String videoId) throws MediaNotFoundException;


    String getVideoRating(String accountId,String videoId);

    int howManyUserVideos(String userId);



}
