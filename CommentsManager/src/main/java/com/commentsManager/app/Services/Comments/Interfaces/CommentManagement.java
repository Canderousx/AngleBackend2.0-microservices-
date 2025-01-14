package com.commentsManager.app.Services.Comments.Interfaces;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import org.apache.coyote.BadRequestException;

import java.io.IOException;

public interface CommentManagement {

    void addComment(Comment comment) throws BadRequestException;

    void removeComment(String id) throws MediaNotFoundException, IOException, ClassNotFoundException;

    void removeVideoComments(String videoId);

}
