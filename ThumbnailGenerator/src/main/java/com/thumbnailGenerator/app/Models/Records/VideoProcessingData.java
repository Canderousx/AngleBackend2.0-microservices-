package com.thumbnailGenerator.app.Models.Records;

public record VideoProcessingData(
        String videoId,
        String rawFilePath,

        String hlsPath,

        String playlistName
) {
}