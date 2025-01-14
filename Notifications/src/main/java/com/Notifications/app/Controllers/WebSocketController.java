package com.Notifications.app.Controllers;

import com.Notifications.app.Services.Notifications.NotificationsManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final NotificationsManagementService notificationsManagementService;

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



}
