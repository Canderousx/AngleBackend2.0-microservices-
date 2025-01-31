package com.statsService.app.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchTime {
    private String userId;

    private String videoId;

    private String status;

    private long timeStarted;

    private long accumulatedWatchTime;

    private long timePaused;



    public VideoView toVideoView(){
        VideoView view = new VideoView();
        view.setTimeWatched(this.accumulatedWatchTime);
        view.setVideoId(this.videoId);
        view.setDate(new Date());
        view.setAccountId(this.userId);
        view.setLocation("undefined");
        return view;
    }
}
