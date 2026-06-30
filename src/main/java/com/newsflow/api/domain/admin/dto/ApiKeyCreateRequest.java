package com.newsflow.api.domain.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ApiKeyCreateRequest {

    @NotBlank
    private String clientName;

    @Min(1) @Max(100_000)
    private int rateLimitPerHour = 1000;

    private LocalDateTime expiresAt;
}
