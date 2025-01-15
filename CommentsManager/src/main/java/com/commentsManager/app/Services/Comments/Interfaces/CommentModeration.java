package com.commentsManager.app.Services.Comments.Interfaces;

public interface CommentModeration {
    void banComment(String id);

    void unbanComment(String id);

    void banAllVideoComments(String videoId);

    void unbanAllVideoComments(String videoId);
    void banAllUserComments(String userId);
    void unbanAllUserComments(String userId);
}
