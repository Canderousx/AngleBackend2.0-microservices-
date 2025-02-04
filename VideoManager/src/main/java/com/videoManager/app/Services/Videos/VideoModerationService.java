package com.videoManager.app.Services.Videos;

import com.videoManager.app.Config.Exceptions.FileServiceException;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Config.Exceptions.UnauthorizedException;
import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.Cache.VideoCache;
import com.videoManager.app.Services.Files.FileDeleterService;
import com.videoManager.app.Services.Kafka.KafkaSenderService;
import com.videoManager.app.Services.Notifications.NotificationGeneratorService;
import com.videoManager.app.Services.Tags.TagSaverService;
import com.videoManager.app.Services.Videos.Interfaces.VideoModerationInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideoModerationService implements VideoModerationInterface {


    private final VideoRetrievalService videoRetrievalService;

    private final VideoRepository videoRepository;

    private final FileDeleterService fileDeleterService;

    private final TagSaverService tagSaverService;

    private final NotificationGeneratorService notificationGenerator;

    private final KafkaSenderService kafkaSenderService;

    private final VideoCache videoCache;

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
        boolean editMode = video.getName() != null && video.getDescription() != null;
        video.setName(metadata.name());
        video.setDescription(metadata.description());
        video.setTags(tagSaverService.setTags(metadata));
        video.setThumbnail(metadata.thumbnail());
        videoRepository.save(video);
        videoCache.removeFromCache(videoCache.getVideoKey(id));
        if(editMode){
            notificationGenerator.videoChangesSaved(
                    video.getAuthorId(),
                    video.getName(),
                    video.getId(),
                    video.getThumbnail()
            );
        }else if(!video.isProcessing()){
            notificationGenerator.videoProcessingFinished(
                    video.getAuthorId(),
                    video.getName(),
                    video.getId(),
                    video.getThumbnail()
            );
        }else{
            notificationGenerator.videoStillProcessing(
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
    public void updateViews(Map<String,Long> data) {
        if(data.isEmpty()){
            return;
        }
        data.forEach((key, value) -> {
            if(value > 0){
                videoRepository.addViews(key,value);
            }
        });
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

    @Override
    public void banUserVideos(String userId) {
        videoRepository.banAllUserVideos(userId);
    }

    @Override
    public void unbanUserVideos(String userId) {
        videoRepository.unbanAllUserVideos(userId);
    }



}
