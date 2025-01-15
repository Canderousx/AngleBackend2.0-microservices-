package com.videoManager.app.Services.Videos;



import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Records.VideoLikesNDislikes;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Models.VideoRating;
import com.videoManager.app.Repositories.VideoRatingRepository;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.API.AuthServiceAPIService;
import com.videoManager.app.Services.Cache.CacheService;
import com.videoManager.app.Services.Cache.PageWrapper;
import com.videoManager.app.Services.Videos.Interfaces.VideoRetrievalInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VideoRetrievalService implements VideoRetrievalInterface {

    private final VideoRepository videoRepository;

    private final VideoRatingRepository videoRatingRepository;

    private final AuthServiceAPIService authService;

    private final CacheService cacheService;

    private void addRandomVideos(List<VideoRecord>currentList, String currentId){
        if(currentList.size() < 10){
            List<String>alreadyIds = new ArrayList<>();
            if(currentList.isEmpty()){
                alreadyIds.add("");
            }else{
                currentList.forEach(video -> {
                    alreadyIds.add(video.getId());
                });
            }
            currentList.addAll(videoRepository.findRandom(alreadyIds,currentId,PageRequest.of(0,10-currentList.size())));
        }
    }



    @Override
    public Page<VideoRecord> getAllVideos(int page,int pageSize) {
        Pageable paginateSettings = PageRequest.of(page,pageSize, Sort.by("datePublished").descending());
        return this.videoRepository.findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(paginateSettings);
    }

    @Override
    public Page<VideoRecord> getUserVideos(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return videoRepository.findByAuthorIdAndProcessingFalse(userId,pageable);
    }

    @Override
    public Page<VideoRecord> getCurrentUserVideos(int page, int pageSize) {
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
    public Video getRawVideo(String videoId) throws MediaNotFoundException {
        Video video = this.videoRepository.findById(videoId).orElse(null);
        if(video!=null){
            return video;
        }
        throw new MediaNotFoundException("Video not found");
    }

    @Override
    public VideoRecord getVideo(String videoId) throws MediaNotFoundException {
        Video video = getRawVideo(videoId);
        return new VideoRecord(
                video.getId(),
                video.getAuthorId(),
                video.getName(),
                video.getDescription(),
                video.getThumbnail(),
                video.getPlaylistName(),
                video.getDatePublished(),
                videoRepository.getViews(videoId),
                video.isProcessing()
        );
    }

    @Override
    public VideoLikesNDislikes getVideoLikesNDislikes(String videoId) {
        long likes = this.countLikes(videoId);
        long dislikes = this.countDislikes(videoId);
        return new VideoLikesNDislikes(likes,dislikes);
    }

    @Override
    public Page<VideoRecord> getLatestVideos(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return videoRepository.findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(pageable);
    }

    @Override
    public Path getStreamPath(String videoId) {
        String url = cacheService.getWithCache(videoId+"__stream_url",videoRepository::getStreamPath);
        Path videoPath = Path.of(url);
        if (!Files.exists(videoPath)) {
            throw new RuntimeException("File .m3u8 for ID: " + videoId+" not found!! Path: "+videoPath);
        }
        return videoPath;
    }

    @Override
    public long countLikes(String videoId) {
        return videoRatingRepository.countLikes(videoId);
    }

    @Override
    public long countDislikes(String videoId) {
        return videoRatingRepository.countDislikes(videoId);
    }

    @Override
    public String getVideoRating(String accountId, String videoId) {
        VideoRating videoRating = videoRatingRepository.findByAccountIdAndVideoId(accountId,videoId).orElse(null);
        if(videoRating == null){
            return "none";
        }
        return videoRating.getRating();
    }

    @Override
    @Cacheable(value = "video_cache",key = "'most_popular'")
    public List<VideoRecord> getMostPopular() {
        int quantity = 4;
        return videoRepository.findMostPopular(quantity);
    }

    @Override
    public Page<VideoRecord> getBySubscribers(int page,int pageSize, String token) {
        List<String>subscribersIds = authService.getRandomSubscribedIds(token,10);
        Pageable pageable = PageRequest.of(page,pageSize);
        return videoRepository.findFromSubscribers(subscribersIds,pageable);
    }

    @Override
    @Cacheable(value = "video_cache",key = "#videoId +'__similar_videos'")
    public List<VideoRecord> getSimilar(String videoId) throws MediaNotFoundException {
        List<VideoRecord>videos = new ArrayList<>();
        Video video = getRawVideo(videoId);
        if(video.getTags().isEmpty()){
            addRandomVideos(videos,videoId);
            return videos;
        }
        Set<String> tagNames = new HashSet<>();
        video.getTags().forEach(tag -> tagNames.add(tag.getName()));
        videos = videoRepository.findSimilar(tagNames,videoId);
        addRandomVideos(videos,videoId);
        return videos;
    }
    @Override
    public int howManyUserVideos(String userId){
        return videoRepository.countUserVideos(userId);
    }


}
