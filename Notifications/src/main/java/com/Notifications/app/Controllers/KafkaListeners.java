package com.Notifications.app.Controllers;


import com.Notifications.app.Models.Records.NotificationRecord;
import com.Notifications.app.Services.JsonUtils;
import com.Notifications.app.Services.Notifications.NotificationSender;
import com.Notifications.app.Services.Notifications.NotificationsManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final NotificationsManagementService notificationsManagementService;


    @KafkaListener(topics = "new_notification",groupId = "notification_service")
    public void newNotification(String data){
        NotificationRecord note = JsonUtils.readJson(data, NotificationRecord.class);
        notificationsManagementService.addNotification(note);
    }





}
