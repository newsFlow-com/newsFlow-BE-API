package com.newsflow.api.domain.notification.repository;

import com.newsflow.api.entity.UserNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {

    @Query("""
            SELECT n FROM UserNotification n
            JOIN FETCH n.article a
            LEFT JOIN FETCH n.subscription s
            WHERE n.user.id = :userId
            ORDER BY n.sentAt DESC
            """)
    List<UserNotification> findByUserIdWithDetails(@Param("userId") UUID userId, Pageable pageable);

    long countByUserIdAndIsReadFalse(UUID userId);

    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") UUID userId);
}
