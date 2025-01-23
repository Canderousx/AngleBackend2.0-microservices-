package com.Notifications.app.Services.Notifications;


import com.Notifications.app.Models.Notification;
import com.Notifications.app.Models.Records.BanData;
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

    public void handleAccountBanned(BanData banData){
        log.info("sending ban alert to {}",banData.reportedId());
        messagingTemplate.convertAndSendToUser(
               banData.reportedId(),
               "queue/accountBanned",
               "You've been banned. Check your email for more details."
        );
    }
}
