package com.commentsManager.app.Services.Comments;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Repositories.CommentRepository;
import com.commentsManager.app.Services.API.AuthServiceAPIService;
import com.commentsManager.app.Services.Cache.CommentCache;
import com.commentsManager.app.Services.Cache.PageWrapper;
import com.commentsManager.app.Services.Comments.Interfaces.CommentRetrieval;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentRetrievalService implements CommentRetrieval {

    private final CommentRepository commentRepository;

    private final CommentCache commentCache;


    @Override
    public Comment getComment(String id) throws MediaNotFoundException{
        return commentRepository.findById(id).orElseThrow(() -> new MediaNotFoundException("Requested comment doesn't exist!" ));
    }

    @Override
    public PageWrapper<Comment> getUserComments(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return new PageWrapper<>(commentRepository.findByAuthorIdOrderByDatePublishedDesc(userId,pageable));
    }

    @Override
    public Long countAllComments(String videoId) {
        String redisKey = commentCache.getCommentCountKey(videoId);
        return commentCache.getFromCacheOrFetch(redisKey, Long.class,() -> commentRepository.countAllVideoComments(videoId));
    }

    @Override
    public PageWrapper<Comment> getVideoComments(String videoId,int page, int pageSize){
        String redisKey = commentCache.getVideoCommentsKey(videoId,page,pageSize);
        Pageable paginateSettings = PageRequest.of(page,pageSize, Sort.by("datePublished").descending());
        return commentCache.getFromCacheOrFetch(redisKey, new TypeReference<PageWrapper<Comment>>() {},
                () -> new PageWrapper<>(this.commentRepository.findByVideoIdAndIsBannedFalseAndParentCommentIdIsNull(videoId,paginateSettings)));
    }

    @Override
    public PageWrapper<Comment> getCommentReplies(String parentCommentId, int page, int pageSize) {
        String redisKey = commentCache.getCommentRepliesKey(parentCommentId,page,pageSize);
        Pageable paginate = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return commentCache.getFromCacheOrFetch(redisKey, new TypeReference<PageWrapper<Comment>>() {},
                () -> new PageWrapper<>(this.commentRepository.findByParentCommentIdAndIsBannedFalse(parentCommentId,paginate)));
    }


}
