package com.videoManager.app.Services.Notifications.Interfaces;

import com.videoManager.app.Models.Records.CommentNotificationData;
import com.videoManager.app.Models.Records.NotificationRecord;

public interface NotificationGenerator {
    void videoBanned(String ownerId,String videoTitle,String videoId,String thumbnail);

    void videoUnbanned(String ownerId,String videoTitle,String videoId,String thumbnail);

    void videoProcessingFinished(String ownerId,String videoTitle,String videoId,String thumbnail);

    void videoChangesSaved(String ownerId,String videoTitle,String videoId,String thumbnail);

    void videoStillProcessing(String ownerId,String videoTitle,String videoId,String thumbnail);

    void videoLacksMetadata(String ownerId,String videoId);

    void thumbnailsReadyToUse(String ownerId,String videoId);

    void newCommentNotification(CommentNotificationData commentNotificationData);
}
