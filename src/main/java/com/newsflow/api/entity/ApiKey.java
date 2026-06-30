package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys",
        indexes = {
                @Index(name = "ix_api_keys_hash", columnList = "key_hash"),
                @Index(name = "ix_api_keys_active", columnList = "is_active"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiKey {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "key_hash", nullable = false, unique = true, length = 64)
    private String keyHash;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "rate_limit_per_hour", nullable = false)
    private int rateLimitPerHour = 1000;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public ApiKey(String keyHash, String clientName, int rateLimitPerHour, LocalDateTime expiresAt) {
        this.keyHash = keyHash;
        this.clientName = clientName;
        this.rateLimitPerHour = rateLimitPerHour;
        this.expiresAt = expiresAt;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public void revoke() { this.isActive = false; }
}
