package com.commentsManager.app.Controllers;

import com.commentsManager.app.Services.Comments.CommentManagementService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final CommentManagementService commentManagementService;

    public KafkaListeners(CommentManagementService commentManagementService) {
        this.commentManagementService = commentManagementService;
    }


    @KafkaListener(topics = "delete_video", groupId = "comments_service")
    public void deleteComments(String id){
        commentManagementService.removeVideoComments(id);
    }

}
