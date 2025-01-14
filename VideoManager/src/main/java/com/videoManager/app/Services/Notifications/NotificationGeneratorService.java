package com.videoManager.app.Services.Notifications;

import com.videoManager.app.Models.EnvironmentVariables;
import com.videoManager.app.Models.Records.CommentNotificationData;
import com.videoManager.app.Models.Records.NotificationRecord;
import com.videoManager.app.Services.API.Interfaces.AuthServiceAPI;
import com.videoManager.app.Services.JsonUtils;
import com.videoManager.app.Services.Kafka.KafkaSenderService;
import com.videoManager.app.Services.Notifications.Interfaces.NotificationGenerator;
import com.videoManager.app.Services.Videos.VideoRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationGeneratorService implements NotificationGenerator {

    private final KafkaSenderService kafkaSenderService;

    private final VideoRetrievalService videoRetrievalService;
    private void sendNotification(NotificationRecord notification){
        String json = JsonUtils.toJson(notification);
        kafkaSenderService.send("new_notification",json);
    };


    @Override
    public void newCommentNotification(CommentNotificationData commentNotificationData) {
        String videoAuthorId = videoRetrievalService.getAuthorId(commentNotificationData.videoId());
        if(!commentNotificationData.authorId().equals(videoAuthorId)){
            sendNotification(new NotificationRecord(
                    videoAuthorId,
                    commentNotificationData.authorUsername()+" has just commented your video!",
                    null,
                    "/api/auth/accounts/media/getAvatar?userId="+commentNotificationData.authorId(),
                    "/watch?v="+commentNotificationData.videoId(),
                    true
            ));
        }
    }

    @Override
    public void videoBanned(String ownerId, String videoTitle, String videoId, String thumbnail) {
        sendNotification(new NotificationRecord(
                ownerId,
                "Your video has been banned!",
                "'"+videoTitle+"' has been banned due to our administration decision.",
                thumbnail,
                "/banInfo?v="+videoId,
                true
        ));
    }

    @Override
    public void videoUnbanned(String ownerId, String videoTitle, String videoId, String thumbnail) {
        sendNotification(new NotificationRecord(
                ownerId,
                "Your video has been unbanned!",
                "Due to our administration decision your video '"+videoTitle+"' has been unbanned! Sorry for troubles;)",
                thumbnail,
                "/watch?v="+videoId,
                true
        ));
    }

    @Override
    public void videoProcessingFinished(String ownerId,String videoTitle, String videoId, String thumbnail) {
        sendNotification(new NotificationRecord(
                ownerId,
                "Your video has been processed successfully!",
                "'"+videoTitle+"' is now public and available",
                thumbnail,
                "/watch?v="+videoId,
                true
        ));
    }

    @Override
    public void videoLacksMetadata(String ownerId, String videoId) {
        sendNotification(new NotificationRecord(
                ownerId,
                "Your latest video lacks some metadata. Solve it now!",
                "Your latest video is fine and ready. It's just lacks some data.",
                null,
                "/upload/metadata?v="+videoId,
                true
        ));
    }

    @Override
    public void thumbnailsReadyToUse(String ownerId, String videoId) {
        sendNotification(new NotificationRecord(
                ownerId,
                "Thumbnails generated and ready",
                null,
                null,
                "/upload/metadata?v="+videoId,
                false

        ));
    }
}
