package com.videoManager.app.Services.Videos.Interfaces;


import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VideoSearchInterface {

    List<VideoProjection> getVideosByTag(String tag);

    Page<VideoProjection> findVideos(String query, int page, int pageSize);

    List<String>searchHelper(String query);
}
