package com.commentsManager.app.Repositories;


import com.commentsManager.app.Models.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<Comment>findByAuthorId(String authorId,Pageable pageable);

    Page<Comment>findByAuthorIdOrderByDatePublishedDesc(String authorId,Pageable pageable);

    Page<Comment> findByVideoIdAndIsBannedFalseAndParentCommentIdIsNull(String videoId, Pageable pageable);

    Page<Comment> findByParentCommentIdAndIsBannedFalse(String parentCommentId,Pageable pageable);

    @Query(value = "SELECT count(*) FROM comment c WHERE c.videoid = :videoId AND isbanned = false",nativeQuery = true)
    long countAllVideoComments(@RequestParam("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comment WHERE videoid = :videoId",nativeQuery = true)
    void deleteVideoComments(@RequestParam("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET isbanned = true WHERE authorid = :accountId",nativeQuery = true)
    void banAllUserComments(@RequestParam("accountId")String accountId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET isbanned = true WHERE id = :commentId",nativeQuery = true)
    void banComment(@RequestParam("commentId")String commentId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET isbanned = false WHERE id = :commentId",nativeQuery = true)
    void unbanComment(@RequestParam("commentId")String commentId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET isbanned = true WHERE videoid = :videoId",nativeQuery = true)
    void banVideoComments(@RequestParam("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET isbanned = false WHERE videoid = :videoId",nativeQuery = true)
    void unbanVideoComments(@RequestParam("videoId")String videoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET isbanned = false WHERE authorid = :accountId",nativeQuery = true)
    void unbanAllUserComments(@RequestParam("accountId")String accountId);



}
