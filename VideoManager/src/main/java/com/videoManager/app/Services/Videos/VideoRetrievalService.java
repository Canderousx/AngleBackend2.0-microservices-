package com.videoManager.app.Services.Videos;



import com.videoManager.app.Config.Exceptions.MediaBannedException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Repositories.Specifications.VideoSpecification;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.API.AuthServiceAPIService;
import com.videoManager.app.Services.Cache.RedisService;
import com.videoManager.app.Services.Videos.Interfaces.VideoRetrievalInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final RedisService redisService;

    private void addRandomVideos(List<VideoProjection>currentList, String currentId){
        if(currentList.size() < 10){
            List<String>alreadyIds = new ArrayList<>();
            if(!currentList.isEmpty()){
                currentList.forEach(video -> {
                    alreadyIds.add(video.getId());
                });
            }
            currentList.addAll(
//                    videoRepository.findRandom(alreadyIds,currentId,PageRequest.of(0,10-currentList.size()),VideoRecord.class));
                    videoRepository.findBy(VideoSpecification.findRandom(alreadyIds,currentId),
                            q -> q.as(VideoProjection.class).page(PageRequest.of(0,10-currentList.size()))).toList());
        }
    }



    @Override
    public Page<VideoProjection> getAllVideos(int page,int pageSize) {
        Pageable paginateSettings = PageRequest.of(page,pageSize, Sort.by("datePublished").descending());
        return this.videoRepository.findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(paginateSettings);
    }

    @Override
    public Page<VideoProjection> getUserVideos(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return videoRepository.findByAuthorIdAndProcessingFalse(userId,pageable);
    }

    @Override
    public Page<VideoProjection> getCurrentUserVideos(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return videoRepository.findByAuthorId(userId,pageable);
    }

    @Override
    @Cacheable(value = "video_cache",key = "#videoId +'__author_id'")
    public String getAuthorId(String videoId) {
        return videoRepository.getAuthorId(videoId);
    }

    @Override
    @Cacheable(value = "video_cache",key = "#videoId +'__raw_video'",unless = "#result == null")
    public Video getRawVideo(String videoId) throws MediaNotFoundException {
        Video video = this.videoRepository.findById(videoId).orElse(null);
        if(video!=null){
            return video;
        }
        throw new MediaNotFoundException("Video not found");
    }

    @Override
    public VideoProjection getVideo(String videoId) throws MediaNotFoundException, MediaBannedException {
        if(videoRepository.isBanned(videoId)){
            throw new MediaBannedException("Video is banned.");
        }
        return videoRepository.findDTOById(videoId).orElse(null);
    }

    @Override
    public Page<VideoProjection> getLatestVideos(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return videoRepository.findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(pageable);
    }

    @Override
    public Path getStreamPath(String videoId) {
        String url = redisService.get(redisService.getStreamUrlCacheKey(videoId),String.class);
        if(url == null){
            url = videoRepository.getStreamPath(videoId);
            redisService.add(redisService.getStreamUrlCacheKey(videoId), Duration.ofDays(30));
        }
        Path videoPath = Path.of(url);
        if (!Files.exists(videoPath)) {
            throw new RuntimeException("File .m3u8 for ID: " + videoId+" not found!! Path: "+videoPath);
        }
        return videoPath;
    }


    @Override
    public List<VideoProjection> getMostPopular(int quantity) {
        Pageable pageable = PageRequest.of(0,quantity);
        return videoRepository.findBy(VideoSpecification.findAllActive(),
                q -> q.as(VideoProjection.class).page(pageable)).getContent();
    }

    @Override
    public Page<VideoProjection> getBySubscribers(int page,int pageSize) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String>subscribersIds = authService.getRandomSubscribedIds(accountId,10);
        System.out.println("SUBSRIBERS IDS: "+subscribersIds.size());
        Pageable pageable = PageRequest.of(page,pageSize);
        return videoRepository.findBy(VideoSpecification.findBySubscribers(subscribersIds),
                q -> q.as(VideoProjection.class).page(pageable));
    }

    @Override
    public List<VideoProjection> getSimilar(String videoId) throws MediaNotFoundException {
        List<VideoProjection>videos;
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
        return videos;
    }
    @Override
    public int howManyUserVideos(String userId){
        return videoRepository.countUserVideos(userId);
    }


}
