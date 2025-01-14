package com.commentsManager.app.Services.Comments.Interfaces;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;

public interface CommentModeration {

    void banComment(String id) throws MediaNotFoundException;

    void unbanComment(String id);
}
