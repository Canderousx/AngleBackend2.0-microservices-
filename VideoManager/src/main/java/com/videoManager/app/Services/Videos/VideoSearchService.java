package com.videoManager.app.Services.Videos;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Repositories.TagRepository;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.Videos.Interfaces.VideoSearchInterface;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VideoSearchService implements VideoSearchInterface {

    private final VideoRepository videoRepository;

    private final TagRepository tagRepository;

    @Override
    public List<VideoProjection> getVideosByTag(String tag) {
        Optional<VideoProjection> tagsVideos = this.videoRepository.findByTag(tag);
        if(tagsVideos.isPresent()){
            return new ArrayList<>(tagsVideos.stream().toList());
        }
        return null;
    }

    @Override
    public Page<VideoProjection> findVideos(String query, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return videoRepository.findByNameContainingOrTagsNameContaining(query,query,pageable);
    }

    @Override
    public List<String> searchHelper(String query) {
        List<String>helpers;
        helpers = tagRepository.findNameContaining(query);
        helpers.addAll(videoRepository.findNameContaining(query));
        return helpers;
    }
}
