package com.newsflow.api.domain.admin.dto;

import com.newsflow.api.entity.ApiKey;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ApiKeyResponse {

    private UUID id;
    private String clientName;
    private boolean isActive;
    private int rateLimitPerHour;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static ApiKeyResponse from(ApiKey key) {
        return ApiKeyResponse.builder()
                .id(key.getId())
                .clientName(key.getClientName())
                .isActive(key.isActive())
                .rateLimitPerHour(key.getRateLimitPerHour())
                .expiresAt(key.getExpiresAt())
                .createdAt(key.getCreatedAt())
                .build();
    }
}
