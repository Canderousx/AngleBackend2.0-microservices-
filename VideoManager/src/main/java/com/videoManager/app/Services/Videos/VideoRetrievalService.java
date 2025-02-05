package com.videoManager.app.Services.Videos;



import com.fasterxml.jackson.core.type.TypeReference;
import com.videoManager.app.Config.Exceptions.MediaBannedException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Repositories.Specifications.VideoSpecification;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.API.AuthServiceAPIService;
import com.videoManager.app.Services.Cache.PageWrapper;
import com.videoManager.app.Services.Cache.VideoCache;
import com.videoManager.app.Services.Videos.Interfaces.VideoRetrievalInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoRetrievalService implements VideoRetrievalInterface {

    private final VideoRepository videoRepository;

    private final AuthServiceAPIService authService;

    private final VideoCache videoCache;

    private void addRandomVideos(List<VideoProjection>currentList, String currentId){
        if(currentList.size() < 10){
            List<String>alreadyIds = new ArrayList<>();
            if(!currentList.isEmpty()){
                currentList.forEach(video -> {
                    alreadyIds.add(video.getId());
                });
            }
            currentList.addAll(
                    videoRepository.findBy(VideoSpecification.findRandom(alreadyIds,currentId),
                            q -> q.as(VideoProjection.class).page(PageRequest.of(0,10-currentList.size()))).toList());
        }
    }

    @Override
    public PageWrapper<VideoProjection> getUserVideos(String userId, int page, int pageSize) {
        String redisKey = videoCache.getVideoPageKey(page,pageSize)+"_"+userId;
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return videoCache.getFromCacheOrFetch(
                redisKey,
                new TypeReference<PageWrapper<VideoProjection>>() {},
                () -> new PageWrapper<>(
                        videoRepository.findByAuthorIdAndProcessingFalseAndNameIsNotNullAndIsBannedFalseAndThumbnailIsNotNull(userId,pageable)));
    }

    @Override
    public PageWrapper<VideoProjection> getCurrentUserVideos(int page, int pageSize) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return new PageWrapper<>(videoRepository.findByAuthorId(userId,pageable));
    }

    @Override
    public String getAuthorId(String videoId) {
        return videoRepository.getAuthorId(videoId);
    }

    @Override
    public Video getRawVideo(String videoId) throws MediaNotFoundException {
        Video video = this.videoRepository.findById(videoId).orElse(null);
        if(video!=null){
            return video;
        }
        throw new MediaNotFoundException("Video not found");
    }

    @Override
    public VideoProjection getVideo(String videoId) throws MediaBannedException {
        String redisKey = videoCache.getVideoKey(videoId);
        VideoProjection video = videoCache.getFromCacheOrFetch(redisKey,VideoProjection.class, () -> videoRepository.findDTOById(videoId).orElse(null)
                ,Duration.ofHours(1));
        if(video.getIsBanned()){
            throw new MediaBannedException("Video is banned.");
        }
        return video;
    }

    @Override
    public PageWrapper<VideoProjection> getLatestVideos(int page, int pageSize) {
        String redisKey = videoCache.getVideoPageKey(page,pageSize)+"_latest";
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return videoCache.getFromCacheOrFetch(redisKey, new TypeReference<PageWrapper<VideoProjection>>() {},
                () -> new PageWrapper<>(videoRepository.findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(pageable))
        );
    }

    @Override
    public Path getStreamPath(String videoId) {
        String redisKey = videoCache.getStreamUrlCacheKey(videoId);
        String url = videoCache.getFromCache(redisKey,String.class);
        if(url == null){
            url = videoRepository.getStreamPath(videoId);
            videoCache.saveToCache(redisKey,url);
        }
        Path videoPath = Path.of(url);
        if (!Files.exists(videoPath)) {
            throw new RuntimeException("File .m3u8 for ID: " + videoId+" not found!! Path: "+videoPath);
        }
        return videoPath;
    }


    @Override
    public List<VideoProjection> getMostPopular(int quantity) {
        String redisKey = videoCache.getVideoListKey()+"_most_popular";
        Pageable pageable = PageRequest.of(0,quantity,Sort.by("views").descending());
        return videoCache.getFromCacheOrFetch(redisKey, new TypeReference<List<VideoProjection>>() {},
                () -> videoRepository.findBy(VideoSpecification.findAllActive(), q -> q.as(VideoProjection.class).page(pageable).getContent())
                );
    }

    @Override
    public PageWrapper<VideoProjection> getBySubscribers(int page,int pageSize) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        String videoRedisKey = videoCache.getVideoPageKey(page,pageSize)+"_bySubs_"+accountId;
        String subsRedisKey = "subs_list:"+accountId;
        List<String>subscribersIds = videoCache.getFromCacheOrFetch(subsRedisKey, new TypeReference<List<String>>() {},
                () -> authService.getRandomSubscribedIds(accountId,10));
        return videoCache.getFromCacheOrFetch(videoRedisKey, new TypeReference<PageWrapper<VideoProjection>>() {},
                () -> new PageWrapper<>(
                        videoRepository.findBy(VideoSpecification.findBySubscribers(subscribersIds),
                                q -> q.as(VideoProjection.class).page(pageable))));
    }

    @Override
    public List<VideoProjection> getSimilar(String videoId) throws MediaNotFoundException {
        String redisKey = videoCache.getVideoListKey()+"similar_"+videoId;
        Pageable pageable = PageRequest.of(0,10,Sort.by("views").descending());
        return videoCache.getFromCacheOrFetch(redisKey, new TypeReference<List<VideoProjection>>() {},() ->{
            Video video;
            try {
                video = getRawVideo(videoId);
            } catch (MediaNotFoundException e) {
                log.error("Video {} not found!!",videoId);
                return null;
            }
            List<VideoProjection> videos = new ArrayList<>();
            if(video.getTags().isEmpty()){
                addRandomVideos(videos,videoId);
                return videos;
            }
            Set<String> tagNames = new HashSet<>();
            video.getTags().forEach(tag -> tagNames.add(tag.getName()));
            Page<VideoProjection> page = videoRepository.findBy(VideoSpecification.findSimilar(tagNames,videoId),
                    q -> q.as(VideoProjection.class).page(pageable));
            videos = page.getContent();
            addRandomVideos(videos,videoId);
            return videos;
        });
    }
    @Override
    public int howManyUserVideos(String userId){
        return videoRepository.countUserVideos(userId);
    }


}
