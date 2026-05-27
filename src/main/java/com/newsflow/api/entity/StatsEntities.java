package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ── DailyArticleStat ──────────────────────────────────────────────
@Entity
@Table(name = "daily_article_stats",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_das_article_date",
                columnNames = {"article_id", "stat_date"}),
        indexes = {
                @Index(name = "ix_das_date", columnList = "stat_date"),
                @Index(name = "ix_das_trending", columnList = "trending_score"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyArticleStat {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount = 0;

    @Column(name = "share_count", nullable = false)
    private int shareCount = 0;

    @Column(name = "trending_score", nullable = false)
    private double trendingScore = 0.0;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── DailyUserStat ─────────────────────────────────────────────────
@Entity
@Table(name = "daily_user_stats",
        indexes = @Index(name = "ix_dus_date", columnList = "stat_date", unique = true))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyUserStat {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "stat_date", nullable = false, unique = true)
    private LocalDate statDate;

    @Column(name = "total_users", nullable = false)
    private int totalUsers;

    @Column(name = "active_users", nullable = false)
    private int activeUsers;

    @Column(name = "new_users", nullable = false)
    private int newUsers;

    @Column(name = "suspended_users", nullable = false)
    private int suspendedUsers;

    @Column(name = "deleted_users", nullable = false)
    private int deletedUsers;

    @Column(name = "kakao_signup_count", nullable = false)
    private int kakaoSignupCount;

    @Column(name = "google_signup_count", nullable = false)
    private int googleSignupCount;

    @Column(name = "churn_rate")
    private Double churnRate;

    @Column(name = "mau")
    private Integer mau;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── PipelineStat ──────────────────────────────────────────────────
@Entity
@Table(name = "pipeline_stats",
        indexes = {
                @Index(name = "ix_ps_dag_started", columnList = "dag_id, started_at"),
                @Index(name = "ix_ps_status", columnList = "status"),
                @Index(name = "ix_ps_started", columnList = "started_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PipelineStat {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "dag_id", nullable = false, length = 100)
    private String dagId;

    @Column(name = "run_id", length = 255)
    private String runId;

    /** success | partial | fail | running */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "collected_count", nullable = false)
    private int collectedCount;

    @Column(name = "duplicate_count", nullable = false)
    private int duplicateCount;

    @Column(name = "error_count", nullable = false)
    private int errorCount;

    @Column(name = "source_count", nullable = false)
    private int sourceCount;

    @Column(name = "duration_seconds")
    private Double durationSeconds;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── ApiRequestLog ─────────────────────────────────────────────────
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