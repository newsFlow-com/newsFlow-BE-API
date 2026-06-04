package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

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
    // ── 도메인 메서드 (Builder 아래에 추가) ──────────────────────────
    public void update(String title, String content, Boolean isPinned,
                       Boolean isActive, String targetGate,
                       LocalDateTime publishedAt, LocalDateTime expiredAt) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (isPinned != null) this.isPinned = isPinned;
        if (isActive != null) this.isActive = isActive;
        if (targetGate != null) this.targetGate = targetGate;
        if (publishedAt != null) this.publishedAt = publishedAt;
        if (expiredAt != null) this.expiredAt = expiredAt;
    }

    public void deactivate() { this.isActive = false; }
}