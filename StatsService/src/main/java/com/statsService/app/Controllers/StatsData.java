package com.statsService.app.Controllers;


import com.statsService.app.Models.Records.VideoViewDetailsRecord;
import com.statsService.app.Services.Subscription.SubscriptionRetrievalService;
import com.statsService.app.Services.VideoView.VideoViewRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class StatsData {

    private final SubscriptionRetrievalService subscriptionRetrievalService;

    private final VideoViewRetrievalService videoViewRetrievalService;

    @RequestMapping(value = "getSubscribedChannelsRandom",method = RequestMethod.GET)
    public List<String> getSubscribedIds(@RequestParam int quantity){
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        return subscriptionRetrievalService.getSubscribedChannelsOrderByRandom(accountId,quantity);
    }

    @RequestMapping(value = "countSubscribers",method = RequestMethod.GET)
    public Long countSubscribers(@RequestParam String id){
        return subscriptionRetrievalService.countSubscribers(id);
    }

    @RequestMapping(value = "countViews",method = RequestMethod.GET)
    public Long countViews(@RequestParam String videoId){
        return videoViewRetrievalService.countViews(videoId);
    }

    @RequestMapping(value = "getViewsPerLocation",method = RequestMethod.GET)
    public List<VideoViewDetailsRecord>getViewsPerLocation(@RequestParam String videoId){
        return videoViewRetrievalService.getViewsDescByLocation(videoId);
    }

}
