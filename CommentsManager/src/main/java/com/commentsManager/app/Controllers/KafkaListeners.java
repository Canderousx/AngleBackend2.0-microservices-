package com.commentsManager.app.Controllers;

import com.commentsManager.app.Models.Records.BanData;
import com.commentsManager.app.Services.Comments.CommentManagementService;
import com.commentsManager.app.Services.Comments.CommentModerationService;
import com.commentsManager.app.Services.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final CommentManagementService commentManagementService;

    private final CommentModerationService commentModerationService;



    @KafkaListener(topics = "delete_video", groupId = "comments_service")
    public void deleteComments(String id){
        commentManagementService.removeVideoComments(id);
    }

    @KafkaListener(topics = "comment_banned",groupId = "comments_service")
    public void banComment(String json){
        BanData banData = JsonUtils.readJson(json, BanData.class);
        commentModerationService.banComment(banData.bannedMediaId());
    }
    @KafkaListener(topics = "account_banned",groupId = "comments_service")
    public void banUserComments(String json){
        BanData banData = JsonUtils.readJson(json, BanData.class);
        commentModerationService.banAllUserComments(banData.reportedId());
    }

}
