package com.Notifications.app.Controllers;


import com.Notifications.app.Models.Records.BanData;
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

    private final NotificationSender notificationSender;


    @KafkaListener(topics = "new_notification",groupId = "notification_service")
    public void newNotification(String data){
        NotificationRecord note = JsonUtils.readJson(data, NotificationRecord.class);
        notificationsManagementService.addNotification(note);
    }

    @KafkaListener(topics = "account_banned",groupId = "notification_service")
    public void accountBanned(String data){
        BanData banData = JsonUtils.readJson(data, BanData.class);
        notificationSender.handleAccountBanned(banData);
    }





}
