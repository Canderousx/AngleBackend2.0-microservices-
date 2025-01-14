package com.Notifications.app.Services.Notifications;


import com.Notifications.app.Models.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final SimpMessagingTemplate messagingTemplate;


    public void sendNotification(Notification notification){
        log.info("sending notification to {}",notification.getOwnerId());
        messagingTemplate.convertAndSendToUser(
                notification.getOwnerId(),
                "queue/notification",
                notification
        );

    }
}
