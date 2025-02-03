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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final VideoUploadService videoUploadService;

    private final NotificationGenerator notificationGenerator;

    private final VideoRepository videoRepository;

    private final VideoModerationService videoModerationService;

    @KafkaListener(topics = "video_processed", groupId = "video_group")
    public void videoProcessed(String json){
        VideoProcessingData vpf = JsonUtils.readJson(json, VideoProcessingData.class);
        videoUploadService.finishProcessing(vpf);
    }

    @KafkaListener(topics = "thumbnails_generated", groupId = "video_group")
    public void thumbnailsGenerated(String json){
        ThumbnailsData data = JsonUtils.readJson(json, ThumbnailsData.class);
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
        Map<String,Long> data = JsonUtils.readJson(json, new TypeReference<Map<String, Long>>() {});
        videoModerationService.updateViews(data);
    }

    @KafkaListener(topics = "video_banned", groupId = "video_group")
    public void videoBanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        videoModerationService.banVideo(data.bannedMediaId());
    }

    @KafkaListener(topics = "video_unbanned", groupId = "video_group")
    public void videoUnbanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        videoModerationService.unbanVideo(data.bannedMediaId());
    }

    @KafkaListener(topics = "account_banned", groupId = "video_group")
    public void accountBanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        videoModerationService.banUserVideos(data.reportedId());
    }

    @KafkaListener(topics = "account_unbanned", groupId = "video_group")
    public void accountUnbanned(String json) throws MediaNotFoundException {
        BanData data = JsonUtils.readJson(json, BanData.class);
        videoModerationService.unbanUserVideos(data.reportedId());
    }





}
