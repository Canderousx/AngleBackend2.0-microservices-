package com.videoManager.app.Services.Videos.Interfaces;


import com.videoManager.app.Models.Projections.VideoProjection;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VideoSearchInterface {

    Page<VideoProjection> getVideosByTag(String tag,int page,int pageSize);

    Page<VideoProjection> findVideos(String query, int page, int pageSize);

    List<String>searchHelper(String query);
}
