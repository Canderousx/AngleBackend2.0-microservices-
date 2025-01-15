package com.commentsManager.app.Services.Comments;

import com.commentsManager.app.Repositories.CommentRepository;
import com.commentsManager.app.Services.Comments.Interfaces.CommentModeration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommentModerationService implements CommentModeration {

    private final CommentRepository commentRepository;

    @Override
    public void banComment(String id) {
        commentRepository.banComment(id);
    }

    @Override
    public void unbanComment(String id) {
        commentRepository.unbanComment(id);
    }

    @Override
    public void banAllVideoComments(String videoId) {
        commentRepository.banVideoComments(videoId);
    }

    @Override
    public void unbanAllVideoComments(String videoId) {
        commentRepository.unbanVideoComments(videoId);
    }

    @Override
    public void banAllUserComments(String userId) {
        commentRepository.banAllUserComments(userId);
    }

    @Override
    public void unbanAllUserComments(String userId) {
        commentRepository.unbanAllUserComments(userId);
    }
}
