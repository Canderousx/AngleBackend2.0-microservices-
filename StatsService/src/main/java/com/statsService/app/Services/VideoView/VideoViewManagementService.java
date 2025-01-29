package com.statsService.app.Services.VideoView;

import com.statsService.app.Models.Records.WatchTimeRecord;
import com.statsService.app.Models.VideoView;
import com.statsService.app.Repositories.VideoViewRepository;
import com.statsService.app.Services.API.ApiNinjaService;
import com.statsService.app.Services.VideoView.Interfaces.VideoViewManagementInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoViewManagementService implements VideoViewManagementInterface {

    private final VideoViewRepository videoViewRepository;

    private final ApiNinjaService apiNinjaService;


    @Override
    public void registerView(WatchTimeRecord watchTime,String ip) {
        VideoView videoView = new VideoView();
        videoView.setVideoId(watchTime.videoId());
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            videoView.setAccountId(null);
        }else{
            videoView.setAccountId(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        videoView.setTimeWatched(watchTime.watchTime());
        videoView.setLocation(apiNinjaService.getLocation(ip));
        videoViewRepository.save(videoView);
    }
}
