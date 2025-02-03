package com.videoManager.app.Controllers;


import com.videoManager.app.Config.Exceptions.MediaBannedException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Services.Videos.VideoRetrievalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideosData {

    private final VideoRetrievalService videoRetrievalService;

    public VideosData(VideoRetrievalService videoRetrievalService) {
        this.videoRetrievalService = videoRetrievalService;
    }

    @RequestMapping(value = "/getVideo",method = RequestMethod.GET)
    public VideoProjection getVideo(@RequestParam String id) throws MediaNotFoundException, MediaBannedException {
        return videoRetrievalService.getVideo(id);
    }
    @RequestMapping(value = "/getUserVideos",method = RequestMethod.GET)
    public Page<VideoProjection>getUserVideos(@RequestParam String id, @RequestParam int page, @RequestParam int pageSize){
        return videoRetrievalService.getUserVideos(id,page,pageSize);
    }

    @RequestMapping(value = "/getCurrentUserVideos",method = RequestMethod.GET)
    public Page<VideoProjection>getCurrentUserVideos(@RequestParam int page, @RequestParam int pageSize){
        return videoRetrievalService.getCurrentUserVideos(page,pageSize);
    }

    @RequestMapping(value = "/getLatestVideos",method = RequestMethod.GET)
    public Page<VideoProjection>getLatestVideos(@RequestParam int page, @RequestParam int pageSize){
        return videoRetrievalService.getLatestVideos(page,pageSize);
    }
    @RequestMapping(value = "/getMostPopular",method = RequestMethod.GET)
    public List<VideoProjection> getMostPopularVideos(@RequestParam int quantity){
        return videoRetrievalService.getMostPopular(quantity);
    }

    @RequestMapping(value = "/getBySubscribed",method = RequestMethod.GET)
    public Page<VideoProjection> getBySubscribed(@RequestParam int page, @RequestParam int pageSize, HttpServletRequest request){
        String token = request.getHeader("Authentication").substring(7);
        Page<VideoProjection> list = videoRetrievalService.getBySubscribers(page,pageSize);
        System.out.println("SUBS VIDEOS: "+list.getContent().size());
        return list;
    }

    @RequestMapping(value = "/getSimilar",method = RequestMethod.GET)
    public List<VideoProjection>getSimilar(@RequestParam String id) throws MediaNotFoundException {
        return videoRetrievalService.getSimilar(id);
    }




}
