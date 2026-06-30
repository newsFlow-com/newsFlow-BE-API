package com.newsflow.api.domain.subscription.dto;

import com.newsflow.api.entity.Subscription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SubscriptionResponse {

    private UUID id;
    private String subscriptionType;
    private String value;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static SubscriptionResponse from(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .subscriptionType(sub.getSubscriptionType())
                .value(sub.getValue())
                .isActive(sub.isActive())
                .createdAt(sub.getCreatedAt())
                .build();
    }
}
