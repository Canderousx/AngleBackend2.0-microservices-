package com.thumbnailGenerator.app.Models.Records;

import java.util.List;

public record ThumbnailsData(
        String videoId,
        List<String>thumbnails
) {
}
