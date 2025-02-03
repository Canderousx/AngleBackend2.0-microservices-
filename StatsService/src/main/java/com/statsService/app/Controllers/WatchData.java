package com.statsService.app.Controllers;

import com.statsService.app.Models.Records.RatingRecord;
import com.statsService.app.Models.Records.VideoStats;
import com.statsService.app.Services.VideoStats.VideoStatsRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/watchData")
@RequiredArgsConstructor
public class WatchData {

    private final VideoStatsRetrievalService videoStatsRetrievalService;

    @RequestMapping(value = "/getViews",method = RequestMethod.GET)
    public Long getViews(@RequestParam String videoId){
        return videoStatsRetrievalService.countViews(videoId);
    }

    @RequestMapping(value = "/videoStats",method = RequestMethod.GET)
    public VideoStats getVideoStats(@RequestParam String id){
        return videoStatsRetrievalService.getVideoStats(id);
    }

    @RequestMapping(value = "/checkRated",method = RequestMethod.GET)
    public RatingRecord checkRated(@RequestParam String id){
        return new RatingRecord(videoStatsRetrievalService.getVideoRating(id));
    }


}
