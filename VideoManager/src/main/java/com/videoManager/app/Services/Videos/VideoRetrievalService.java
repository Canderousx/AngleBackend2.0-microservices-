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

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoRetrievalService implements VideoRetrievalInterface {

    private final VideoRepository videoRepository;

    private final AuthServiceAPIService authServiceAPI;

    private final VideoCache videoCache;


    /*
    Helper method, which checks out whether the list of the videos has the desired size specified in 'maxSize' value.
    If not, it adds some random videos to fill the list.
     */

    private void addRandomVideos(List<VideoProjection>currentList, String currentId,int maxSize){
        int currentSize = currentList.size();
        if(currentSize < maxSize){
            int howManyToAdd = maxSize - currentSize;
            log.info("Number of videos is less than desired {}",maxSize);
            List<String>alreadyIds = new ArrayList<>();
            if(!currentList.isEmpty()){
                currentList.forEach(video -> {
                    alreadyIds.add(video.getId());
                });
            }
            currentList.addAll(
                    videoRepository.findBy(VideoSpecification.findRandom(alreadyIds,currentId),
                            q -> q.as(VideoProjection.class).page(PageRequest.of(0,howManyToAdd))).getContent());
        }
        log.info("Number of videos equals or exceeds {}, adding random aborted",maxSize);
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
    public List<VideoProjection> getMostPopular(int quantity) {
        String redisKey = videoCache.getVideoListKey()+"_most_popular";
        Pageable pageable = PageRequest.of(0,quantity);
        return videoCache.getFromCacheOrFetch(redisKey, new TypeReference<List<VideoProjection>>() {},
                () -> videoRepository.findBy(VideoSpecification.findMostPopular(), q -> q.as(VideoProjection.class).page(pageable).getContent())
                );
    }

    /*

    Method returns a page of videos that are owned by the accounts logged user is currently subscribing.
    Subscription data is stored within 'Auth-Service' microservice - that's why the method need to download the data from the Auth-Service API.
    To reduce the amount of the requests, downloaded data is stored in redis for 1 hour.

    When subscribed channels ids are known, the page of videos is being downloaded from the database and also saved to redis as cache for 5 minutes (if it's not empty or null).
     */

    @Override
    public PageWrapper<VideoProjection> getBySubscribers(int page,int pageSize) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        String videoRedisKey = videoCache.getVideoPageKey(page,pageSize)+"_bySubs_"+accountId;
        String subsRedisKey = "subs_list:"+accountId;
        return videoCache.getFromCacheOrFetch(videoRedisKey, new TypeReference<PageWrapper<VideoProjection>>() {}, () -> {
            List<String>subscribersIds = videoCache.getFromCacheOrFetch(subsRedisKey, new TypeReference<List<String>>() {}, () -> {
                List<String> ids = authServiceAPI.getRandomSubscribedIds(accountId,10);
                if(ids == null || ids.isEmpty()){
                    return null;
                }
                return ids;
                },Duration.ofHours(1));
            return new PageWrapper<>(
                    videoRepository.findBy(VideoSpecification.findBySubscribers(subscribersIds),
                            q -> q.as(VideoProjection.class).page(pageable)));
        });
    }


    /*

     Method returns list of videos similar to the one with the given id based on their tags. If the size of the list
     is less than 10 or the video with the given id has no tags -> random videos will be added as well.
     Generated list is being cached in redis for a default time - 5 minutes.

     */
    @Override
    public List<VideoProjection> getSimilar(String videoId) throws MediaNotFoundException {
        String redisKey = videoCache.getVideoListKey()+"similar_"+videoId;
        int quantity = 10;
        Pageable pageable = PageRequest.of(0,quantity,Sort.by("views").descending());
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
                log.info("Video without tags - adding {} random videos to the list.",quantity);
                addRandomVideos(videos,videoId,quantity);
                return videos;
            }
            Set<String> tagNames = new HashSet<>();
            video.getTags().forEach(tag -> tagNames.add(tag.getName()));
            log.info("Video tags loaded. Finding similar videos from a database.");
            Page<VideoProjection> page = videoRepository.findBy(VideoSpecification.findSimilar(tagNames,videoId),
                    q -> q.as(VideoProjection.class).page(pageable));
            videos = new ArrayList<>(page.getContent());
            log.info("Found {} similar videos",videos.size());
            addRandomVideos(videos,videoId,quantity);
            log.info("Total videos count in a list {}",videos.size());
            return videos;
        });
    }
    @Override
    public int howManyUserVideos(String userId){
        return videoRepository.countUserVideos(userId);
    }


}
