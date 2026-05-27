package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// ── Bookmark ──────────────────────────────────────────────────────
@Entity
@Table(name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_bm_user_article",
                columnNames = {"user_id", "article_id"}),
        indexes = @Index(name = "ix_bm_user", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public Bookmark(User user, Article article) {
        this.user = user;
        this.article = article;
        this.createdAt = LocalDateTime.now();
    }
}

// ── UserCategory ──────────────────────────────────────────────────
@Entity
@Table(name = "user_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_uc_user_category",
                columnNames = {"user_id", "category_id"}),
        indexes = @Index(name = "ix_uc_user", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "preference_weight", nullable = false)
    private int preferenceWeight = 5;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public UserCategory(User user, Category category, int preferenceWeight) {
        this.user = user;
        this.category = category;
        this.preferenceWeight = preferenceWeight;
        this.createdAt = LocalDateTime.now();
    }

    public void updateWeight(int weight) {
        this.preferenceWeight = weight;
    }
}

// ── ShareLog ──────────────────────────────────────────────────────
@Entity
@Table(name = "share_logs",
        indexes = {
                @Index(name = "ix_sl_user", columnList = "user_id"),
                @Index(name = "ix_sl_target", columnList = "target_type, target_id"),
                @Index(name = "ix_sl_channel", columnList = "channel"),
                @Index(name = "ix_sl_created", columnList = "created_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** article | chart | stock */
    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_id", nullable = false, columnDefinition = "uuid")
    private UUID targetId;

    /** kakao | link | clipboard | twitter */
    @Column(nullable = false, length = 30)
    private String channel;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public ShareLog(User user, String targetType, UUID targetId,
                    String channel, String ipAddress) {
        this.user = user;
        this.targetType = targetType;
        this.targetId = targetId;
        this.channel = channel;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }
}