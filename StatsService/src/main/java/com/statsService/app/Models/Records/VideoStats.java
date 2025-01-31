package com.statsService.app.Models.Records;

public record VideoStats(
        String videoId,
        long likes,
        long dislikes,
        long views
) {
}
