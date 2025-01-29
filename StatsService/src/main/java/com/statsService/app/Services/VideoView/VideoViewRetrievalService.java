package com.statsService.app.Services.VideoView;

import com.statsService.app.Models.Records.VideoViewDetailsRecord;
import com.statsService.app.Repositories.VideoViewRepository;
import com.statsService.app.Services.VideoView.Interfaces.VideoViewRetrievalInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoViewRetrievalService implements VideoViewRetrievalInterface {

    private final VideoViewRepository videoViewRepository;
    @Override
    @Cacheable(value = "stats_cache",key = "#videoId +'_views'",unless = "#result == 0")
    public long countViews(String videoId) {
        return videoViewRepository.countByVideoId(videoId);
    }

    @Override
    @Cacheable(value = "stats_cache",key = "#videoId +'_views_'+ #location",unless = "#result == 0")
    public long countViews(String videoId, String location) {
        return videoViewRepository.countByVideoIdAndLocation(videoId,location);
    }

    @Override
    @Cacheable(value = "stats_cache",key = "#videoId +'_viewsDescByLocation")
    public List<VideoViewDetailsRecord> getViewsDescByLocation(String videoId) {
        return videoViewRepository.findLocationAndNumberOfViewsByVideoId(videoId, PageRequest.of(0,8));
    }
}
