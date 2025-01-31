package com.statsService.app.Services.VideoStats.Interfaces;

import com.statsService.app.Models.Records.RatingRecord;
import com.statsService.app.Models.Records.VideoStats;
import com.statsService.app.Models.Records.VideoViewDetailsRecord;

import java.util.List;

public interface VideoStatsRetrievalInterface {

    long countViews(String videoId);

    long countViews(String videoId,String location);

    long countLikes(String videoId);

    long countDislikes(String videoId);

    String getVideoRating(String videoId);



    List<VideoViewDetailsRecord>getViewsDescByLocation(String videoId);

    VideoStats getVideoStats(String videoId);




}
