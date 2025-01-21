package com.Notifications.app.Controllers;

import com.Notifications.app.Models.Notification;
import com.Notifications.app.Services.Notifications.NotificationsManagementService;
import com.Notifications.app.Services.Notifications.NotificationsRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final NotificationsManagementService notificationsManagementService;

    private final NotificationsRetrievalService retrievalService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/markAsSeen")
    public void markAsSeen(Map<String,Object> payload,Principal principal){
        String notificationId = (String) payload.get("id");
        notificationsManagementService.markAsSeen(notificationId,principal);
    }

    @MessageMapping("/clearAllNotifications")
    public void clearNotifications(Principal principal){
        String userId = principal.getName();
        notificationsManagementService.removeAll(userId);
    }

    @MessageMapping("/getMyNotifications")
    public void getMyNotifications(Map<String,Object>payload,Principal principal){
        int page = (int) payload.get("page");
        int pageSize = (int) payload.get("pageSize");
        Page<Notification> myNotes = retrievalService.getUserNotifications(page,pageSize,principal);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),"queue/myNotifications",myNotes);
    }




}
