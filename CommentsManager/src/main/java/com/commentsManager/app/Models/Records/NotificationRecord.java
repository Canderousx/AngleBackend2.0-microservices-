package com.commentsManager.app.Models.Records;

public record NotificationRecord(
        String ownerId,
        String title,
        String content,
        String image,
        String url
) {
}
