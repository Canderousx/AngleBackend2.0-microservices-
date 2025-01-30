package com.statsService.app.Services.VideoView.Interfaces;

public interface VideoViewManagementInterface {


    void onPlay(String userId,String videoId);

    void onPause(String userId,String videoId);

    void onEnded(String userId,String videoId);

}
