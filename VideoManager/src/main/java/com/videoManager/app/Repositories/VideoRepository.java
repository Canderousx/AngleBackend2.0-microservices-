package com.videoManager.app.Repositories;

import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Records.VideoRecord;
import com.videoManager.app.Models.Video;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {

    Optional<Video> findByName(String name);

    Optional<Video> findByDatePublished(Date datePublished);

    Optional<Video>findByRawPath(String rawPath);
    Page<VideoRecord> findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(Pageable page);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM video_tags WHERE video_id = :videoId", nativeQuery = true)
    void deleteTagAssociations(@Param("videoId") String videoId);


    @Query(value = "SELECT * FROM Video v WHERE FIND_IN_SET(:tag, v.tags) > 0 AND v.thumbnail IS NOT NULL AND v.name IS NOT NULL AND v.isbanned = false", nativeQuery = true)
    Optional<VideoProjection> findByTag(String tag);

    Page<VideoRecord> findByAuthorIdAndProcessingFalse(String authorId, Pageable pageable);

    Page<VideoRecord> findByAuthorId(String authorId,Pageable pageable);

    @Query("""
    SELECT new com.videoManager.app.Models.Records.VideoRecord(
        v.id,
        v.authorId,
        v.name,
        v.description,
        v.thumbnail,
        v.playlistName,
        v.datePublished,
        v.views,
        v.processing
    )
    FROM Video v
    JOIN v.tags t
    WHERE v.isBanned = false 
      AND t.name IN :tagNames 
      AND v.id != :currentVideoId 
      AND v.thumbnail IS NOT NULL 
      AND v.name IS NOT NULL
      AND processing = false
    GROUP BY v.id
    ORDER BY function('RAND')
""")
    List<VideoRecord> findSimilar(@Param("tagNames") Set<String> tagNames, @Param("currentVideoId") String videoId);



    @Query("""
    SELECT new com.videoManager.app.Models.Records.VideoRecord(
        v.id,
        v.authorId,
        v.name,
        v.description,
        v.thumbnail,
        v.playlistName,
        v.datePublished,
        v.views,
        v.processing
    )
    FROM Video v
    WHERE v.isBanned = false 
      AND v.id NOT IN :videoIds 
      AND v.thumbnail IS NOT NULL 
      AND v.name IS NOT NULL 
      AND v.id != :currentVideoId
      AND processing = false
    ORDER BY function('RAND')
""")
    List<VideoRecord> findRandom(@Param("videoIds") List<String> videoIds,
                                 @Param("currentVideoId") String currentId,
                                 Pageable pageable);



    @Query("""
    SELECT new com.videoManager.app.Models.Records.VideoRecord(
        v.id,
        v.authorId,
        v.name,
        v.description,
        v.thumbnail,
        v.playlistName,
        v.datePublished,
        v.views,
        v.processing
    )
    FROM Video v
    WHERE v.isBanned = false 
      AND v.thumbnail IS NOT NULL 
      AND v.name IS NOT NULL
      AND processing = false
    ORDER BY v.views DESC
""")
    List<VideoRecord> findMostPopular(Pageable pageable);


    @Query("""
    SELECT new com.videoManager.app.Models.Records.VideoRecord(
        v.id,
        v.authorId,
        v.name,
        v.description,
        v.thumbnail,
        v.playlistName,
        v.datePublished,
        v.views,
        v.processing
    )
    FROM Video v
    JOIN v.tags t
    WHERE v.isBanned = false 
      AND t.name IN :tagNames
      AND v.thumbnail IS NOT NULL
      AND processing = false
    GROUP BY v.id
    ORDER BY function('RAND')
""")
    List<VideoRecord> findRecommended(@Param("tagNames") Set<String> tagNames, Pageable pageable);



    List<VideoRecord> findByNameContaining(String name);

    @Query(value = "SELECT DISTINCT LOWER(v.name) FROM video v WHERE v.isbanned = false AND LOWER(v.name) LIKE CONCAT(:vName, '%') ORDER BY LOWER(v.name) ASC", nativeQuery = true)
    List<String> findNameContaining(@Param("vName") String vName);

    @Query(value = "SELECT DISTINCT v.* FROM video v LEFT JOIN video_tags vt ON v.id = vt.video_id LEFT JOIN tag t ON vt.tag_id = t.id WHERE v.isbanned = false AND v.name LIKE %:name% OR t.name LIKE %:tagName%", nativeQuery = true)
    Page<VideoProjection> findByNameContainingOrTagsNameContaining(@Param("name") String name, @Param("tagName") String tagName, Pageable pageable);


    @Query("""
    SELECT new com.videoManager.app.Models.Records.VideoRecord(
        v.id,
        v.authorId,
        v.name,
        v.description,
        v.thumbnail,
        v.playlistName,
        v.datePublished,
        v.views,
        v.processing
    )
    FROM Video v
    WHERE v.authorId IN :subIds 
      AND v.isBanned = false
      AND v.processing = false
    ORDER BY v.datePublished DESC
""")
    Page<VideoRecord> findFromSubscribers(@Param("subIds") List<String> subIds, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET isbanned = true WHERE id = :videoId",nativeQuery = true)
    void banVideo(@Param("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET isbanned = true WHERE authorid = :accountId", nativeQuery = true)
    void banAllUserVideos(@RequestParam("accountId") String accountId);


    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET isbanned = false WHERE id = :videoId", nativeQuery = true)
    void unbanVideo(@RequestParam("videoId") String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET isbanned = false WHERE authorid = :accountId", nativeQuery = true)
    void unbanAllUserVideos(@RequestParam("accountId") String accountId);

    @Query(value = "SELECT count(*) FROM video v WHERE v.authorId = :accountId", nativeQuery = true)
    int countUserVideos(@Param("accountId") String accountId);


    @Query(value = "SELECT authorId FROM video WHERE id = :videoId",nativeQuery = true)
    String getAuthorId(@Param("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET views = views + 1 WHERE id = :videoId", nativeQuery = true)
    void registerView(@Param("videoId") String videoId);

    @Query(value = "SELECT v.views FROM video v WHERE v.id = :videoId",nativeQuery = true)
    long getViews(@Param("videoId")String videoId);

    @Query(value = "SELECT stream_path FROM video WHERE id = :videoId",nativeQuery = true)
    String getStreamPath(@Param("videoId")String videoId);

}
