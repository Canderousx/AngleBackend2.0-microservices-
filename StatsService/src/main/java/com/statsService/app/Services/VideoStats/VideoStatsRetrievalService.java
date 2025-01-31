package com.statsService.app.Services.VideoStats;

import com.statsService.app.Models.Records.RatingRecord;
import com.statsService.app.Models.Records.VideoStats;
import com.statsService.app.Models.Records.VideoViewDetailsRecord;
import com.statsService.app.Models.VideoRating;
import com.statsService.app.Repositories.VideoRatingRepository;
import com.statsService.app.Repositories.VideoViewRepository;
import com.statsService.app.Services.Cache.RedisService;
import com.statsService.app.Services.VideoStats.Interfaces.VideoStatsRetrievalInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoStatsRetrievalService implements VideoStatsRetrievalInterface {

    private final VideoViewRepository videoViewRepository;

    private final VideoRatingRepository videoRatingRepository;

    private final RedisService redisService;

    @Override
    public long countViews(String videoId) {
        String cacheKey = redisService.getViewsCacheKey(videoId);
        Long views = redisService.get(cacheKey, Long.class);
        if(views == null){
            views = videoViewRepository.countByVideoId(videoId);
            redisService.add(cacheKey,views,Duration.ofHours(1));
        }
        return views;
    }

    @Override
    public long countViews(String videoId, String location) {
        return videoViewRepository.countByVideoIdAndLocation(videoId,location);
    }

    @Override
    public long countLikes(String videoId) {
        String cacheKey = redisService.getLikesCacheKey(videoId);
        Long likes = redisService.get(cacheKey, Long.class);
        if(likes == null){
            likes = videoRatingRepository.countLikes(videoId);
            redisService.add(cacheKey,likes,Duration.ofHours(1));
        }
        return likes;
    }

    @Override
    public long countDislikes(String videoId) {
        String cacheKey = redisService.getDislikesCacheKey(videoId);
        Long dislikes = redisService.get(cacheKey, Long.class);
        if(dislikes == null){
            dislikes = videoRatingRepository.countDislikes(videoId);
            redisService.add(cacheKey,dislikes,Duration.ofHours(1));
        }
        return dislikes;
    }

    private String getVideoRatingFromRedis(String accountId,String videoId){
        String unsavedRatingKey = redisService.getUnsavedRatingKey(accountId,videoId);
        String ratingCacheKey = redisService.getRatingCacheKey(accountId,videoId);
        VideoRating videoRating = redisService.get(unsavedRatingKey, VideoRating.class);
        if(videoRating != null){
            return videoRating.getRating();
        }
        videoRating = redisService.get(ratingCacheKey, VideoRating.class);
        if(videoRating != null){
            return videoRating.getRating();
        }
        return null;
    }

    private String getVideoRatingFromDatabase(String accountId,String videoId){
        Optional<VideoRating> videoRatingOpt = videoRatingRepository.findByAccountIdAndVideoId(accountId,videoId);
        if(videoRatingOpt.isPresent()){
            redisService.add(redisService.getRatingCacheKey(accountId,videoId),videoRatingOpt.get(),Duration.ofMinutes(5));
            return videoRatingOpt.get().getRating();
        }
        return null;
    }

    @Override
    public String getVideoRating(String videoId) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(accountId == null){
            return "none";
        }
        String rating = getVideoRatingFromRedis(accountId,videoId);
        if(rating == null){
            rating = getVideoRatingFromDatabase(accountId,videoId);
        }
        if(rating == null){
            return "none";
        }
        return rating;
    }

    @Override
    @Cacheable(value = "stats_cache",key = "#videoId +'_viewsDescByLocation")
    public List<VideoViewDetailsRecord> getViewsDescByLocation(String videoId) {
        return videoViewRepository.findLocationAndNumberOfViewsByVideoId(videoId, PageRequest.of(0,8));
    }

    @Override
    public VideoStats getVideoStats(String videoId) {
        return new VideoStats(
                videoId,
                countLikes(videoId),
                countDislikes(videoId),
                countViews(videoId)
        );
    }
}
