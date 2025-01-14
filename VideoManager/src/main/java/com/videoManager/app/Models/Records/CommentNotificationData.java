package com.videoManager.app.Models.Records;

import java.util.Date;

public record CommentNotificationData(
        String authorId,

        String authorUsername,

        String videoId,

        Date datePublished
) {
}
