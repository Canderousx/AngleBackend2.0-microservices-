package com.videoManager.app.Controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.videoManager.app.Config.Exceptions.MediaNotFoundException;
import com.videoManager.app.Models.Records.BanData;
import com.videoManager.app.Models.Records.CommentNotificationData;
import com.videoManager.app.Models.ThumbnailsData;
import com.videoManager.app.Models.Records.VideoProcessingData;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.JsonUtils;
import com.videoManager.app.Services.Notifications.Interfaces.NotificationGenerator;
import com.videoManager.app.Services.Videos.VideoModerationService;
import com.videoManager.app.Services.Videos.VideoUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    private final VideoUploadService videoUploadService;

    private final NotificationGenerator notificationGenerator;

    private final VideoRepository videoRepository;

    private final VideoModerationService videoModerationService;

    @KafkaListener(topics = "video_processed", groupId = "video_group")
    public void videoProcessed(String json){
        VideoProcessingData vpf = JsonUtils.readJson(json, VideoProcessingData.class);
        log.info("Video {} has been processed succesfully",vpf.videoId());
        videoUploadService.finishProcessing(vpf);
    }

    @KafkaListener(topics = "thumbnails_generated", groupId = "video_group")
    public void thumbnailsGenerated(String json){
        ThumbnailsData data = JsonUtils.readJson(json, ThumbnailsData.class);
        log.info("Thumbnails have been generated for {}",data.getVideoId());
        videoUploadService.thumbnailsGeneratedProcess(data);
        notificationGenerator.thumbnailsReadyToUse(videoRepository.getAuthorId(data.getVideoId()),data.getVideoId());
    }

    @KafkaListener(topics = "new_comment_added", groupId = "video_group")
    public void newCommentAdded(String json){
        CommentNotificationData data = JsonUtils.readJson(json, CommentNotificationData.class);
        notificationGenerator.newCommentNotification(data);
    }

    @KafkaListener(topics = "views_update", groupId = "video_group")
    public void updateViews(String json){
        log.info("Received views updates.");
        Map<String,Long> data = JsonUtils.readJson(json, new TypeReference<Map<String, Long>>() {});
        videoModerationService.updateViews(data);
    }

    @KafkaListener(topics = "video_banned", groupId = "video_group")
    public void videoBanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        log.info("Received video ban event. Video id: {}",data.bannedMediaId());
        videoModerationService.banVideo(data.bannedMediaId());
    }

    @KafkaListener(topics = "video_unbanned", groupId = "video_group")
    public void videoUnbanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        log.info("Received video unban event. Video id: {}",data.bannedMediaId());
        videoModerationService.unbanVideo(data.bannedMediaId());
    }

    @KafkaListener(topics = "account_banned", groupId = "video_group")
    public void accountBanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        log.info("Received account ban event. Account id: {}",data.reportedId());
        videoModerationService.banUserVideos(data.reportedId());
    }

    @KafkaListener(topics = "account_unbanned", groupId = "video_group")
    public void accountUnbanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        log.info("Received account unban event. Account id: {}",data.reportedId());
        videoModerationService.unbanUserVideos(data.reportedId());
    }





}
