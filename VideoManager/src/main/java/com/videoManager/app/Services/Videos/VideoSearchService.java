package com.videoManager.app.Services.Videos;
import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Repositories.Specifications.VideoSpecification;
import com.videoManager.app.Repositories.TagRepository;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.Videos.Interfaces.VideoSearchInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class VideoSearchService implements VideoSearchInterface {

    private final VideoRepository videoRepository;

    private final TagRepository tagRepository;

    @Override
    public Page<VideoProjection> getVideosByTag(String tag,int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return videoRepository.findBy(VideoSpecification.findByTag(tag),
                q -> q.as(VideoProjection.class).page(pageable));
    }

    @Override
    public Page<VideoProjection> findVideos(String query, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize, Sort.by("name").descending());
        return videoRepository.findBy(VideoSpecification.searchVideos(query),
                q -> q.as(VideoProjection.class).page(pageable));
    }

    @Override
    public List<String> searchHelper(String query) {
        List<String>helpers;
        Pageable pageable = PageRequest.of(0,15);
        helpers = tagRepository.findNameContaining(query,pageable);
        helpers.addAll(videoRepository.findNameContaining(query,pageable));
        return helpers;
    }
}
