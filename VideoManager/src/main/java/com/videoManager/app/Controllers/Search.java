package com.videoManager.app.Controllers;


import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Services.Videos.VideoSearchService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class Search {

    private final VideoSearchService searchService;

    public Search(VideoSearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    public Page<VideoProjection>findVideos(@RequestParam String q,@RequestParam int page, @RequestParam int pageSize){
        return searchService.findVideos(q,page,pageSize);
    }

    @RequestMapping(value = "/helper",method = RequestMethod.GET)
    public List<String>searchHelper(@RequestParam String q){
        return searchService.searchHelper(q);
    }
}
