package com.commentsManager.app.Services.Comments;

import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Models.Records.CommentNotificationData;
import com.commentsManager.app.Repositories.CommentRepository;
import com.commentsManager.app.Services.Comments.Interfaces.CommentManagement;
import com.commentsManager.app.Services.JsonUtils;
import com.commentsManager.app.Services.Kafka.KafkaSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentManagementService implements CommentManagement {

    private final CommentRepository commentRepository;

    private final CommentRetrievalService commentRetrievalServiceImpl;

    private final KafkaSenderService kafkaSenderService;


    @Override
    public void addComment(Comment comment) throws BadRequestException {
        if(comment !=null){
            comment.setDatePublished(new Date());
            this.commentRepository.save(comment);
            CommentNotificationData cnd = new CommentNotificationData(
                    comment.getAuthorId(),
                    comment.getAuthorName(),
                    comment.getVideoId(),
                    comment.getDatePublished()
            );
            String json = JsonUtils.toJson(cnd);
            kafkaSenderService.send("new_comment_added",json);
        }else{
            throw new RuntimeException("Unable to add new comment. It's NULL");
        }
    }

    @Override
    public void removeComment(String id) throws MediaNotFoundException, IOException, ClassNotFoundException {
        Comment toDelete = commentRetrievalServiceImpl.getComment(id);
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if(accountId.equals(toDelete.getAuthorId()) || isAdmin){
            this.commentRepository.delete(toDelete);
        }else{
            throw new BadRequestException("Unauthorized");
        }
    }

    @Override
    public void removeVideoComments(String videoId) {
        commentRepository.deleteVideoComments(videoId);
    }
}
