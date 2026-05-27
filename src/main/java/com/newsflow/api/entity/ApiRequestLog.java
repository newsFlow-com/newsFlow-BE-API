package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_request_logs",
        indexes = {
                @Index(name = "ix_arl_endpoint_created", columnList = "endpoint, created_at"),
                @Index(name = "ix_arl_gate", columnList = "gate"),
                @Index(name = "ix_arl_status_code", columnList = "status_code"),
                @Index(name = "ix_arl_response_time", columnList = "response_time_ms"),
                @Index(name = "ix_arl_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiRequestLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    /** user | admin */
    @Column(nullable = false, length = 10)
    private String gate;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 255)
    private String endpoint;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(name = "response_time_ms", nullable = false)
    private int responseTimeMs;

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public ApiRequestLog(String gate, String method, String endpoint,
                         int statusCode, int responseTimeMs,
                         UUID userId, String ipAddress, String userAgent) {
        this.gate = gate;
        this.method = method;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = LocalDateTime.now();
    }
}