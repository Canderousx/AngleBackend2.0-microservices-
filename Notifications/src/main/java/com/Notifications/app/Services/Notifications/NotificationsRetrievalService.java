package com.Notifications.app.Services.Notifications;

import com.Notifications.app.Models.Notification;
import com.Notifications.app.Repositories.NotificationRepository;
import com.Notifications.app.Services.Notifications.Interfaces.NotificationsRetrievalInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class NotificationsRetrievalService implements NotificationsRetrievalInterface {

    private final NotificationRepository notificationRepository;

    public NotificationsRetrievalService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Override
    public Notification getLatest() {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        return notificationRepository.getLatestNotification(accountId);
    }

    @Override
    public Page<Notification> getUserNotifications(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return notificationRepository.findByOwnerIdAndForUserTrueOrderByDatePublishedDesc(userId,pageable);
    }

    @Override
    public Page<Notification> getAllUserNotifications(int page, int pageSize) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page,pageSize);
        return notificationRepository.findByOwnerIdAndForUserTrueOrderByDatePublishedDesc(
                accountId,
                pageable
        );
    }

    @Override
    public Page<Notification> getUnseenUserNotifications(int page, int pageSize) {
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page,pageSize);
        return notificationRepository.findByOwnerIdAndSeenFalseOrderByDatePublishedDesc(
                accountId,
                pageable
        );
    }
}
