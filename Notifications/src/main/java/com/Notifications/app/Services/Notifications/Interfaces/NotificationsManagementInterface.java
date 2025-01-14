package com.Notifications.app.Services.Notifications.Interfaces;

import com.Notifications.app.Models.Records.NotificationRecord;

import java.security.Principal;

public interface NotificationsManagementInterface {

    void addNotification(NotificationRecord notificationRecord);

    void markAsSeen(String id,Principal principal);

    void markAsUnseen(String id);

    void removeNotification(String id);


}
