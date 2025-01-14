package com.commentsManager.app.Models.Records;

import java.util.Date;

public record CommentRecord(
        String id,
        String authorName,
        Date datePublished,
        String authorAvatar,
        String content,
        int likes,
        int dislikes
) {
}
