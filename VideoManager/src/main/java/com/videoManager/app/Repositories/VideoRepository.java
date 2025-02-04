package com.videoManager.app.Repositories;

import com.videoManager.app.Models.Projections.VideoProjection;
import com.videoManager.app.Models.Video;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Repository
public interface VideoRepository extends JpaRepository<Video, String>, JpaSpecificationExecutor<Video> {

    Optional<VideoProjection> findByName(String name);

    Optional<VideoProjection> findByDatePublished(Date datePublished);

    Optional<VideoProjection>findByRawPath(String rawPath);

    Page<VideoProjection> findAllByThumbnailIsNotNullAndNameIsNotNullAndIsBannedFalseAndProcessingFalse(Pageable page);

    Page<VideoProjection> findByAuthorIdAndProcessingFalseAndNameIsNotNullAndIsBannedFalseAndThumbnailIsNotNull(String authorId, Pageable pageable);

    Page<VideoProjection> findByAuthorId(String authorId,Pageable pageable);

    @Query(value = "SELECT DISTINCT LOWER(v.name) FROM video v WHERE v.isbanned = false AND LOWER(v.name) LIKE CONCAT(:vName, '%') ORDER BY LOWER(v.name) ASC", nativeQuery = true)
    List<String> findNameContaining(@Param("vName") String vName,Pageable pageable);

    @Query(value = "SELECT * FROM video WHERE id = :id",nativeQuery = true)
    Optional<VideoProjection>findDTOById(@Param("id") String id);

    @Query(value = "SELECT count(*) FROM video v WHERE v.authorId = :accountId", nativeQuery = true)
    int countUserVideos(@Param("accountId") String accountId);

    @Query(value = "SELECT isbanned FROM video WHERE id = :id",nativeQuery = true)
    boolean isBanned(@Param("id")String id);
    @Query(value = "SELECT stream_path FROM video WHERE id = :videoId",nativeQuery = true)
    String getStreamPath(@Param("videoId")String videoId);

    @Query(value = "SELECT authorId FROM video WHERE id = :videoId",nativeQuery = true)
    String getAuthorId(@Param("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM video_tags WHERE video_id = :videoId", nativeQuery = true)
    void deleteTagAssociations(@Param("videoId") String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET views = views + :views WHERE id = :videoId",nativeQuery = true)
    void addViews(@Param("videoId")String id, @Param("views")long viewsToAdd);

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

    @Modifying
    @Transactional
    @Query(value = "UPDATE video SET views = views + 1 WHERE id = :videoId", nativeQuery = true)
    void registerView(@Param("videoId") String videoId);
}
