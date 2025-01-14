package com.videoManager.app.Services.Videos;

import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Models.Ratings;
import com.videoManager.app.Models.Records.NotificationRecord;
import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Models.VideoRating;
import com.videoManager.app.Repositories.VideoRatingRepository;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.Cache.CacheService;
import com.videoManager.app.Services.Files.FileDeleterService;
import com.videoManager.app.Services.JsonUtils;
import com.videoManager.app.Services.Kafka.KafkaSenderService;
import com.videoManager.app.Services.Notifications.NotificationGeneratorService;
import com.videoManager.app.Services.Tags.TagSaverService;
import com.videoManager.app.Services.Videos.Interfaces.VideoModerationInterface;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class VideoModerationService implements VideoModerationInterface {

    private final Logger log = LogManager.getLogger(VideoModerationService.class);

    private final VideoRetrievalService videoRetrievalService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final VideoRepository videoRepository;

    private final VideoRatingRepository videoRatingRepository;

    private final FileDeleterService fileDeleterService;

    private final TagSaverService tagSaverService;

    private final NotificationGeneratorService notificationGenerator;

    private final CacheService cacheService;

    private final KafkaSenderService kafkaSenderService;

    private boolean doesVideoExist(String videoId){
        return videoRepository.existsById(videoId);
    }

    private boolean checkIfOwner(String videoId){
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        String authorId = videoRetrievalService.getAuthorId(videoId);
        return currentUserId.equals(authorId);
    }

    @Override
    public void setMetadata(String id, VideoDetails metadata) throws MediaNotFoundException, UnauthorizedException {
        if(!checkIfOwner(id)){
            throw new UnauthorizedException("Unauthorized: You're not an owner of the video!");
        }
        Video video = videoRetrievalService.getRawVideo(id);
        video.setName(metadata.name());
        video.setDescription(metadata.description());
        video.setTags(tagSaverService.setTags(metadata));
        video.setThumbnail(metadata.thumbnail());
        videoRepository.save(video);
        if(!video.isProcessing()){
            notificationGenerator.videoProcessingFinished(
                    video.getAuthorId(),
                    video.getName(),
                    video.getId(),
                    video.getThumbnail()
            );
        }
    }

    @Override
    public void setThumbnail(String id, String tbUrl) throws MediaNotFoundException {
        Video video = videoRetrievalService.getRawVideo(id);
        video.setThumbnail(tbUrl);
        videoRepository.save(video);
    }

    @Override
    public void registerView(String videoId){
        videoRepository.registerView(videoId);
    }

    @Override
    public void removeVideo(String id) throws MediaNotFoundException, FileServiceException, UnauthorizedException {
        if(!checkIfOwner(id)){
            throw new UnauthorizedException("Unauthorized: You're not an owner of the video!");
        }
        kafkaSenderService.send(
                "delete_video",
                id
        );
        Video video = videoRetrievalService.getRawVideo(id);
        fileDeleterService.deleteVideoFiles(video);
        videoRepository.deleteTagAssociations(video.getId());
        videoRepository.delete(video);
    }

    @Override
    public void banVideo(String videoId) throws MediaNotFoundException {
        Video toBan = videoRetrievalService.getRawVideo(videoId);
        toBan.setBanned(true);
        this.videoRepository.save(toBan);
        notificationGenerator.videoBanned(
                toBan.getAuthorId(),
                toBan.getName(),
                toBan.getId(),
                toBan.getThumbnail()
        );
    }

    @Override
    public void unbanVideo(String videoId) throws MediaNotFoundException {
        Video toUnban = videoRetrievalService.getRawVideo(videoId);
        toUnban.setBanned(false);
        this.videoRepository.save(toUnban);
        notificationGenerator.videoUnbanned(
                toUnban.getAuthorId(),
                toUnban.getName(),
                toUnban.getId(),
                toUnban.getThumbnail()
        );
    }

    private void rateVideo(String videoId,String rating) throws MediaNotFoundException {
        if(!doesVideoExist(videoId)){
            throw new MediaNotFoundException("Requested video not found!");
        }
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!videoRatingRepository.existsByAccountIdAndVideoId(userId,videoId)){
            VideoRating videoRating = new VideoRating();
            videoRating.setVideoId(videoId);
            videoRating.setAccountId(userId);
            videoRating.setRating(rating);
            videoRatingRepository.save(videoRating);
        }else{
            String currentRating = videoRetrievalService.getVideoRating(userId,videoId);
            if(currentRating.equalsIgnoreCase(rating)){
                return;
            }
            videoRatingRepository.updateRating(userId,videoId,rating);
        }
    }

    @Override
    public void dislikeVideo(String videoId) throws MediaNotFoundException {
        rateVideo(videoId,Ratings.DISLIKE.name());
    }
    @Override
    public void likeVideo(String videoId) throws MediaNotFoundException {
        rateVideo(videoId,Ratings.LIKE.name());
    }

    @Override
    public void removeRating(String videoId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        videoRatingRepository.deleteByAccountIdAndVideoId(userId,videoId);
    }
}
