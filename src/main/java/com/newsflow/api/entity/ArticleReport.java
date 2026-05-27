package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

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