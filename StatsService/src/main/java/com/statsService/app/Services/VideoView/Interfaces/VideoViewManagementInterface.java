package com.statsService.app.Services.VideoView.Interfaces;

import com.statsService.app.Models.Records.WatchTimeRecord;

public interface VideoViewManagementInterface {

    void registerView(WatchTimeRecord watchTime,String ip);

}
