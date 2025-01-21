package com.Notifications.app.Services.Notifications.Interfaces;

import com.Notifications.app.Models.Notification;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface NotificationsRetrievalInterface {

    Notification getLatest();

    Page<Notification>getAllUserNotifications(int page, int pageSize);

    Page<Notification>getUserNotifications(int page, int pageSize);

    Page<Notification>getUserNotifications(int page, int pageSize, Principal principal);

    Page<Notification>getUnseenUserNotifications(int page, int pageSize);




}
