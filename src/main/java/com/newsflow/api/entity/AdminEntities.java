package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

// ── AdminLog ──────────────────────────────────────────────────────
@Entity
@Table(name = "admin_logs",
        indexes = {
                @Index(name = "ix_al_admin_created", columnList = "admin_id, created_at"),
                @Index(name = "ix_al_target", columnList = "target_type, target_id"),
                @Index(name = "ix_al_action", columnList = "action"),
                @Index(name = "ix_al_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(name = "target_id", columnDefinition = "uuid")
    private UUID targetId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_value", columnDefinition = "jsonb")
    private Map<String, Object> beforeValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_value", columnDefinition = "jsonb")
    private Map<String, Object> afterValue;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public AdminLog(User admin, String action, String targetType,
                    UUID targetId, Map<String, Object> beforeValue,
                    Map<String, Object> afterValue, String ipAddress) {
        this.admin = admin;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }
}

// ── CollectLog ────────────────────────────────────────────────────
@Entity
@Table(name = "collect_logs",
        indexes = {
                @Index(name = "ix_cl_source_started", columnList = "source_id, started_at"),
                @Index(name = "ix_cl_status", columnList = "status"),
                @Index(name = "ix_cl_started", columnList = "started_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    /** success | fail | partial */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "collected_count", nullable = false)
    private int collectedCount;

    @Column(name = "duplicate_count", nullable = false)
    private int duplicateCount;

    @Column(name = "error_count", nullable = false)
    private int errorCount;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── ContentQualityLog ─────────────────────────────────────────────
@Entity
@Table(name = "content_quality_logs",
        indexes = {
                @Index(name = "ix_cql_article", columnList = "article_id"),
                @Index(name = "ix_cql_check_type", columnList = "check_type"),
                @Index(name = "ix_cql_is_correct", columnList = "is_correct"),
                @Index(name = "ix_cql_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentQualityLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    /** ai_category | duplicate | keyword | stock_link */
    @Column(name = "check_type", nullable = false, length = 30)
    private String checkType;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "original_value", columnDefinition = "jsonb")
    private Map<String, Object> originalValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "correction", columnDefinition = "jsonb")
    private Map<String, Object> correction;

    @Column(name = "checked_by", columnDefinition = "uuid")
    private UUID checkedBy;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── ArticleReport ─────────────────────────────────────────────────
@Entity
@Table(name = "article_reports",
        indexes = {
                @Index(name = "ix_ar_article", columnList = "article_id"),
                @Index(name = "ix_ar_status", columnList = "status"),
                @Index(name = "ix_ar_reporter", columnList = "reporter_id"),
                @Index(name = "ix_ar_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleReport {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    /** spam | misinformation | inappropriate | duplicate | other */
    @Column(nullable = false, length = 30)
    private String reason;

    @Column(columnDefinition = "text")
    private String detail;

    /** pending | reviewing | resolved | dismissed */
    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "resolved_by", columnDefinition = "uuid")
    private UUID resolvedBy;

    @Column(columnDefinition = "text")
    private String resolution;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    public void resolve(UUID adminId, String resolution) {
        this.status = "resolved";
        this.resolvedBy = adminId;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
    }

    public void dismiss(UUID adminId, String resolution) {
        this.status = "dismissed";
        this.resolvedBy = adminId;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
    }
}

// ── Notice ────────────────────────────────────────────────────────
@Entity
@Table(name = "notices",
        indexes = {
                @Index(name = "ix_notices_published", columnList = "published_at"),
                @Index(name = "ix_notices_target", columnList = "target_gate"),
                @Index(name = "ix_notices_pinned", columnList = "is_pinned"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /** user | admin | all */
    @Column(name = "target_gate", nullable = false, length = 10)
    private String targetGate = "user";

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    public Notice(String title, String content, User author,
                  boolean isPinned, String targetGate,
                  LocalDateTime publishedAt, LocalDateTime expiredAt) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.isPinned = isPinned;
        this.targetGate = targetGate != null ? targetGate : "user";
        this.publishedAt = publishedAt;
        this.expiredAt = expiredAt;
    }
}

// ── Banner ────────────────────────────────────────────────────────
@Entity
@Table(name = "banners",
        indexes = {
                @Index(name = "ix_banners_position_active", columnList = "position, is_active"),
                @Index(name = "ix_banners_start_end", columnList = "start_at, end_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(name = "link_url", columnDefinition = "text")
    private String linkUrl;

    /** main_top | sidebar | article_bottom | popup */
    @Column(nullable = false, length = 30)
    private String position;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "click_count", nullable = false)
    private int clickCount = 0;

    @Column(name = "impression_count", nullable = false)
    private int impressionCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}