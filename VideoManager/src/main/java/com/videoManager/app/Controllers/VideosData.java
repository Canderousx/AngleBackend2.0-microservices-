package com.videoManager.app.Controllers;


import com.videoManager.app.Config.Exceptions.MediaBannedException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.RateRecord;
import com.videoManager.app.Models.Records.VideoLikesNDislikes;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Services.Videos.VideoRetrievalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideosData {

    private final VideoRetrievalService videoRetrievalService;

    public VideosData(VideoRetrievalService videoRetrievalService) {
        this.videoRetrievalService = videoRetrievalService;
    }

    @RequestMapping(value = "/getVideo",method = RequestMethod.GET)
    public VideoRecord getVideo(@RequestParam String id) throws MediaNotFoundException, MediaBannedException {
        return videoRetrievalService.getVideo(id);
    }
    @RequestMapping(value = "/getUserVideos",method = RequestMethod.GET)
    public Page<VideoRecord>getUserVideos(@RequestParam String id, @RequestParam int page, @RequestParam int pageSize){
        return videoRetrievalService.getUserVideos(id,page,pageSize);
    }

    @RequestMapping(value = "/getCurrentUserVideos",method = RequestMethod.GET)
    public Page<VideoRecord>getCurrentUserVideos(@RequestParam int page, @RequestParam int pageSize){
        return videoRetrievalService.getCurrentUserVideos(page,pageSize);
    }

    @RequestMapping(value = "/getLatestVideos",method = RequestMethod.GET)
    public Page<VideoRecord>getLatestVideos(@RequestParam int page, @RequestParam int pageSize){
        return videoRetrievalService.getLatestVideos(page,pageSize);
    }
    @RequestMapping(value = "/getMostPopular",method = RequestMethod.GET)
    public List<VideoRecord> getMostPopularVideos(@RequestParam int quantity){
        return videoRetrievalService.getMostPopular(quantity);
    }

    @RequestMapping(value = "/getBySubscribed",method = RequestMethod.GET)
    public Page<VideoRecord> getBySubscribed(@RequestParam int page, @RequestParam int pageSize, HttpServletRequest request){
        String token = request.getHeader("Authentication").substring(7);
        return videoRetrievalService.getBySubscribers(page,pageSize,token);
    }

    @RequestMapping(value = "/getSimilar",method = RequestMethod.GET)
    public List<VideoRecord>getSimilar(@RequestParam String id) throws MediaNotFoundException {
        return videoRetrievalService.getSimilar(id);
    }

    @RequestMapping(value = "getLikesNDislikes",method = RequestMethod.GET)
    public VideoLikesNDislikes getLikesNDislikes(@RequestParam String id){
        return videoRetrievalService.getVideoLikesNDislikes(id);
    }

    @RequestMapping(value = "/checkRated",method = RequestMethod.GET)
    public RateRecord checkRated(@RequestParam String v){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new RateRecord(videoRetrievalService.getVideoRating(userId,v));
    }



}
