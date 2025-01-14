package com.videoManager.app.Repositories;


import com.videoManager.app.Models.ThumbnailsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThumbnailsDataRepository extends JpaRepository<ThumbnailsData, String> {
    Optional<ThumbnailsData>findByVideoId(String videoId);
}
