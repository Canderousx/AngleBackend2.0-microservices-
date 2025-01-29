package com.statsService.app.Repositories;


import com.statsService.app.Models.Projections.LocationViewCount;
import com.statsService.app.Models.Records.VideoViewDetailsRecord;
import com.statsService.app.Models.VideoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoViewRepository extends JpaRepository<VideoView,String> {

    long countByVideoId(String videoId);

    long countByVideoIdAndLocation(String videoId,String location);

    Page<VideoView>findByVideoIdAndLocation(String videoId, String location, Pageable pageable);

    @Query("""
       SELECT v.location AS location, COUNT(v) AS viewCount FROM VideoView v 
       WHERE v.videoId = :videoId 
       GROUP BY v.location 
       ORDER BY COUNT(v) DESC
       """)
    List<VideoViewDetailsRecord> findLocationAndNumberOfViewsByVideoId(@Param("videoId") String videoId, Pageable pageable);


}
