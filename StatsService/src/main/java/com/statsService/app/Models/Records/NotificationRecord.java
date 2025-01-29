package com.statsService.app.Models.Records;

public record NotificationRecord(
        String ownerId,
        String title,
        String content,
        String image,
        String url,
        boolean forUser
) {
}
