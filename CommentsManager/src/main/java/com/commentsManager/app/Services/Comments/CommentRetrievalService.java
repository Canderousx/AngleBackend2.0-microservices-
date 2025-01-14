package com.commentsManager.app.Services.Comments;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Models.Records.Account;
import com.commentsManager.app.Repositories.CommentRepository;
import com.commentsManager.app.Services.API.AuthServiceAPIService;
import com.commentsManager.app.Services.Cache.CacheService;
import com.commentsManager.app.Services.Cache.PageWrapper;
import com.commentsManager.app.Services.Comments.Interfaces.CommentRetrieval;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentRetrievalService implements CommentRetrieval {

    private final CommentRepository commentRepository;

    private final AuthServiceAPIService authServiceAPIService;

    private final CacheService cacheService;

    @Override
    public Comment getComment(String id) throws MediaNotFoundException{
        return commentRepository.findById(id).orElseThrow(() -> new MediaNotFoundException("Requested comment doesn't exist!" ));
    }

    @Override
    public PageWrapper<Comment> getUserComments(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize,Sort.by("datePublished").descending());
        return cacheService.getPageWithCache("user_comments",userId+"__"+page+"__"+pageSize,commentRepository::findByAuthorIdOrderByDatePublishedDesc);
    }

    @Override
    public Page<Comment> getVideoComments(String videoId,int page, int pageSize){
        Pageable paginateSettings = PageRequest.of(page,pageSize, Sort.by("datePublished").descending());
        Page<Comment> pageComments = this.commentRepository.findByVideoId(videoId,paginateSettings);
        if(!pageComments.isEmpty()){
            return pageComments;
        }else{
            return Page.empty();
        }
    }

    @Override
    public int getTotalCommentsNum(String videoId) {
        return commentRepository.countAllVideoComments(videoId);
    }

}
