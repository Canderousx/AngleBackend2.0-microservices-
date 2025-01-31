package com.statsService.app.Services.VideoStats.Interfaces;

public interface VideoStatsManagementInterface {


    void onPlay(String userId,String videoId);

    void onPause(String userId,String videoId);

    void onEnded(String userId,String videoId);

    void likeVideo(String videoId);

    void dislikeVideo(String videoId);

    void removeRating(String videoId);

}
