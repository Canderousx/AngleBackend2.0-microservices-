package com.Notifications.app.Services.Notifications;


import com.Notifications.app.Models.Notification;
import com.Notifications.app.Models.Records.NotificationRecord;
import com.Notifications.app.Repositories.NotificationRepository;
import com.Notifications.app.Services.Notifications.Interfaces.NotificationsManagementInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsManagementService implements NotificationsManagementInterface {

    private final NotificationRepository notificationRepository;

    private final NotificationSender notificationSender;

    @Override
    public void addNotification(NotificationRecord notificationRecord) {
        Notification note = new Notification();
        note.setOwnerId(notificationRecord.ownerId());
        note.setImage(notificationRecord.image());
        note.setUrl(notificationRecord.url());
        note.setContent(notificationRecord.content());
        note.setTitle(notificationRecord.title());
        note.setSeen(false);
        note.setForUser(notificationRecord.forUser());
        note.setDatePublished(new Date());
        notificationRepository.save(note);
        notificationSender.sendNotification(note);
    }

    @Override
    public void markAsSeen(String id,Principal principal) {
        if(principal.getName().equals(notificationRepository.getOwnerId(id))){
            notificationRepository.markAsSeen(id);
            return;
        }
        log.warn("Unauthorized attempt: You're not an owner of the notification!");
    }

    @Override
    public void markAsUnseen(String id) {
        notificationRepository.markAsUnseen(id);
    }

    @Override
    public void removeNotification(String id) {
        notificationRepository.deleteById(id);
    }
}
