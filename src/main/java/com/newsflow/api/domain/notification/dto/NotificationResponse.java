package com.newsflow.api.domain.notification.dto;

import com.newsflow.api.entity.UserNotification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID articleId;
    private String articleTitle;
    private String thumbnailUrl;
    private String subscriptionType;
    private String subscriptionValue;
    private boolean isRead;
    private LocalDateTime sentAt;

    public static NotificationResponse from(UserNotification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .articleId(n.getArticle().getId())
                .articleTitle(n.getArticle().getTitle())
                .thumbnailUrl(n.getArticle().getThumbnailUrl())
                .subscriptionType(n.getSubscription() != null
                        ? n.getSubscription().getSubscriptionType() : null)
                .subscriptionValue(n.getSubscription() != null
                        ? n.getSubscription().getValue() : null)
                .isRead(n.isRead())
                .sentAt(n.getSentAt())
                .build();
    }
}
