package com.commentsManager.app.Services.Comments.Interfaces;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Services.Cache.PageWrapper;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface CommentRetrieval {

    Comment getComment(String id) throws MediaNotFoundException, IOException, ClassNotFoundException;

    PageWrapper<Comment> getUserComments(String userId, int page, int pageSize);

    Page<Comment> getVideoComments(String videoId, int page, int pageSize) throws IOException, ClassNotFoundException, MediaNotFoundException;

    int getTotalCommentsNum(String videoId);




}
