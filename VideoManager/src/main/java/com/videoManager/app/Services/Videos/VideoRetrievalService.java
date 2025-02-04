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
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
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
        PageWrapper<VideoProjection> videos = videoCache.getFromCache(redisKey,new TypeReference<PageWrapper<VideoProjection>>() {});
        if(videos != null){
            return videos;
        }
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        videos = new PageWrapper<>(videoRepository.findByAuthorIdAndProcessingFalseAndNameIsNotNullAndIsBannedFalseAndThumbnailIsNotNull(userId,pageable));
        videoCache.saveToCache(redisKey, videos,Duration.ofMinutes(2));
        return videos;
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
        VideoProjection video = videoCache.getFromCache(redisKey, VideoProjection.class);
        if(video != null){
            return video;
        }
        if(videoRepository.isBanned(videoId)){
            throw new MediaBannedException("Video is banned.");
        }
        video = videoRepository.findDTOById(videoId).orElse(null);
        videoCache.saveToCache(redisKey,video,Duration.ofHours(1));
        return video;
    }

    @Override
    public PageWrapper<VideoProjection> getLatestVideos(int page, int pageSize) {
        String redisKey = videoCache.getVideoPageKey(page,pageSize)+"_latest";
        PageWrapper<VideoProjection> videos = videoCache.getFromCache(redisKey, new TypeReference<PageWrapper<VideoProjection>>() {});
        if(videos != null){
            return videos;
        }
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        videos = new PageWrapper<>(videoRepository.findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(pageable));
        if(videos.getContent().size() > 5){
            videoCache.saveToCache(redisKey,videos);
        }
        return videos;
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
        List<VideoProjection> videos = videoCache.getFromCache(redisKey, new TypeReference<List<VideoProjection>>() {});
        if(videos != null){
            return videos;
        }
        Pageable pageable = PageRequest.of(0,quantity,Sort.by("views").descending());
        videos = videoRepository.findBy(VideoSpecification.findAllActive(),
                q -> q.as(VideoProjection.class).page(pageable)).getContent();
        if(videos.size() >= 5){
            videoCache.saveToCache(redisKey,videos);
        }
        return videos;
    }

    @Override
    public PageWrapper<VideoProjection> getBySubscribers(int page,int pageSize) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();

        String redisKey = videoCache.getVideoPageKey(page,pageSize)+"_bySubs_"+accountId;
        PageWrapper<VideoProjection> videos = videoCache.getFromCache(redisKey, new TypeReference<PageWrapper<VideoProjection>>() {});
        if(videos != null){
            return videos;
        }
        List<String>subscribersIds = authService.getRandomSubscribedIds(accountId,10);
        Pageable pageable = PageRequest.of(page,pageSize);
        videos = new PageWrapper<>(videoRepository.findBy(VideoSpecification.findBySubscribers(subscribersIds),
                q -> q.as(VideoProjection.class).page(pageable)));
        if(videos.getContent().size() >= 5){
            videoCache.saveToCache(redisKey,videos);
        }
        return videos;
    }

    @Override
    public List<VideoProjection> getSimilar(String videoId) throws MediaNotFoundException {
        String redisKey = videoCache.getVideoListKey()+"similar_"+videoId;
        List<VideoProjection>videos = videoCache.getFromCache(redisKey, new TypeReference<List<VideoProjection>>() {});
        if(videos != null){
            return videos;
        }

        Video video = getRawVideo(videoId);
        if(video.getTags().isEmpty()){
            videos = new ArrayList<>();
            addRandomVideos(videos,videoId);
            return videos;
        }
        Set<String> tagNames = new HashSet<>();
        video.getTags().forEach(tag -> tagNames.add(tag.getName()));
        Page<VideoProjection> page = videoRepository.findBy(VideoSpecification.findSimilar(tagNames,videoId),
                q -> q.as(VideoProjection.class).page(PageRequest.of(0,10)));
        videos = new ArrayList<>(page.getContent());
        addRandomVideos(videos,videoId);
        if(videos.size() >= 10){
            videoCache.saveToCache(redisKey,videos);
        }
        return videos;
    }
    @Override
    public int howManyUserVideos(String userId){
        return videoRepository.countUserVideos(userId);
    }


}
