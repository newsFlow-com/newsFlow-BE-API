package com.newsflow.api.domain.notification.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.notification.dto.NotificationResponse;
import com.newsflow.api.domain.notification.repository.UserNotificationRepository;
import com.newsflow.api.entity.UserNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserNotificationRepository notificationRepository;

    public List<NotificationResponse> getMyNotifications(UUID userId, int size) {
        return notificationRepository
                .findByUserIdWithDetails(userId, PageRequest.of(0, Math.min(size, 50)))
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        UserNotification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!n.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        n.markAsRead();
    }

    @Transactional
    public int markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsRead(userId);
    }
}
