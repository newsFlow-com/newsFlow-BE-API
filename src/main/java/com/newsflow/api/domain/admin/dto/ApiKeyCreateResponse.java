package com.newsflow.api.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ApiKeyCreateResponse {

    private UUID id;
    private String clientName;
    private String rawKey;
    private int rateLimitPerHour;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
