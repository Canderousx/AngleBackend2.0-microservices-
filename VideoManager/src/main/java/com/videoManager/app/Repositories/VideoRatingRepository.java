package com.videoManager.app.Repositories;


import com.videoManager.app.Models.VideoRating;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRatingRepository extends JpaRepository<VideoRating,String> {

    boolean existsByAccountIdAndVideoId(String accountId,String videoId);

    void deleteByAccountIdAndVideoId(String accountId,String videoId);

    Optional<VideoRating>findByAccountIdAndVideoId(String accountId,String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE video_rating r SET r.rating = :rating WHERE r.account_id = :accountId AND r.video_id = :videoId", nativeQuery = true)
    void updateRating(@Param("accountId") String accountId, @Param("videoId") String videoId, @Param("rating") String rating);


    @Query(value = "SELECT COUNT(*) FROM video_rating WHERE video_id = :videoId AND rating = 'like'", nativeQuery = true)
    long countLikes(@Param("videoId") String videoId);

    @Query(value = "SELECT COUNT(*) FROM video_rating WHERE video_id = :videoId AND rating = 'dislike'", nativeQuery = true)
    long countDislikes(@Param("videoId") String videoId);



}
