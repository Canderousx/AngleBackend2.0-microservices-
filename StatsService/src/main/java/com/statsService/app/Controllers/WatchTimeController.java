package com.statsService.app.Controllers;
import com.statsService.app.Services.VideoStats.VideoStatsManagementService;
import com.statsService.app.Services.VideoStats.VideoStatsRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WatchTimeController {

    private final VideoStatsManagementService videoStatsManagementService;

    private final VideoStatsRetrievalService videoStatsRetrievalService;

    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/onPlay")
    public void onPlayEvent(Map<String,Object> payload, Principal principal){
        String videoId = (String) payload.get("videoId");
        log.info("On Play: "+videoId);
        if(videoId == null || videoId.isEmpty()){
            log.error("Unable to handle onPlay event. VideoId is null!!");
            return;
        }
        videoStatsManagementService.onPlay(principal.getName(), videoId);
    }

    @MessageMapping("/onPause")
    public void onPauseEvent(Map<String,Object> payload, Principal principal){
        String videoId = (String) payload.get("videoId");
        log.info("On Pause: "+videoId);
        if(videoId == null || videoId.isEmpty()){
            log.error("Unable to handle onPause event. VideoId is null!!");
            return;
        }
        videoStatsManagementService.onPause(principal.getName(), videoId);
    }

    @MessageMapping("/onEnded")
    public void onEndedEvent(Map<String,Object> payload, Principal principal){
        String videoId = (String) payload.get("videoId");
        log.info("On Ended: "+videoId);
        if(videoId == null || videoId.isEmpty()){
            log.error("Unable to handle onEnded event. VideoId is null!!");
            return;
        }
        videoStatsManagementService.onEnded(principal.getName(), videoId);
    }

    @MessageMapping("/rateVideo")
    public void likeVideo(Map<String,Object> payload,Principal principal){
        if(principal.getName().contains("ANONYMOUS")){
            return;
        }
        String rating = (String) payload.get("rating");

    }








}
