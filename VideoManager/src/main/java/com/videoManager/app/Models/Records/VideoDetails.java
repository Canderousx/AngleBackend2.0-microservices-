package com.videoManager.app.Models.Records;

import com.videoManager.app.Models.Tag;

import java.util.ArrayList;
import java.util.List;

public record VideoDetails(
        String name,
        String description,
        List<Tag>tags,
        String thumbnail
) {
}
