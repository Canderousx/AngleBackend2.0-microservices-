package com.videoManager.app.Controllers;


import com.videoManager.app.Models.Records.CommentNotificationData;
import com.videoManager.app.Models.ThumbnailsData;
import com.videoManager.app.Models.Records.VideoProcessingData;
import com.videoManager.app.Repositories.ThumbnailsDataRepository;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.JsonUtils;
import com.videoManager.app.Services.Notifications.Interfaces.NotificationGenerator;
import com.videoManager.app.Services.Videos.VideoModerationService;
import com.videoManager.app.Services.Videos.VideoUploadService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final VideoUploadService videoUploadService;


    private final NotificationGenerator notificationGenerator;

    private final VideoRepository videoRepository;

    public KafkaListeners(VideoUploadService videoUploadService, NotificationGenerator notificationGenerator, VideoRepository videoRepository) {
        this.videoUploadService = videoUploadService;
        this.notificationGenerator = notificationGenerator;
        this.videoRepository = videoRepository;
    }
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



}
