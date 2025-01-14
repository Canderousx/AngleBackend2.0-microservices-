package com.Notifications.app.Repositories;

import com.Notifications.app.Models.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification,String> {

    Page<Notification> findByOwnerIdAndSeenFalseOrderByDatePublishedDesc(String ownerId, Pageable pageable);

    Page<Notification> findByOwnerIdAndForUserTrueOrderByDatePublishedDesc(String ownerId, Pageable pageable);


    @Query(value = "SELECT owner_id FROM notification WHERE id = :id",nativeQuery = true)
    String getOwnerId(@Param("id")String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE notification SET seen = true WHERE id = :noteId",nativeQuery = true)
    void markAsSeen(@Param("noteId")String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE notification SET seen = false WHERE id = :noteId",nativeQuery = true)
    void markAsUnseen(@Param("noteId")String id);

    @Query(value = "SELECT n FROM Notification n WHERE n.ownerId = :accountId ORDER BY n.datePublished DESC")
    Notification getLatestNotification(@Param("accountId") String accountId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Notification WHERE owner_id = :accountId AND for_user = true",nativeQuery = true)
    void clearAll(@Param("accountId")String accountId);

}
