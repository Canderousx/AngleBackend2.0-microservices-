package com.commentsManager.app.Services.Comments;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Repositories.CommentRepository;
import com.commentsManager.app.Services.Comments.Interfaces.CommentModeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentModerationService implements CommentModeration {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentModerationService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }
    @Override
    public void banComment(String id) throws MediaNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new MediaNotFoundException("Comment doesn't exist"));
        comment.setBanned(true);
        commentRepository.save(comment);
    }

    @Override
    public void unbanComment(String id) {
        commentRepository.unbanComment(id);

    }
}
