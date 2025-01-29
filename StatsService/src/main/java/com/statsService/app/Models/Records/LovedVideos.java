package com.statsService.app.Models.Records;

public record LovedVideos(
        String accountId,
        String... videosIds
) {
}
