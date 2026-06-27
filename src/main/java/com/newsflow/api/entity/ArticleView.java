package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "article_views",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_av_user_article",
                columnNames = {"user_id", "article_id"}),
        indexes = @Index(name = "ix_av_user_viewed", columnList = "user_id, viewed_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleView {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "article_id", nullable = false, columnDefinition = "uuid")
    private UUID articleId;

    @Column(name = "viewed_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime viewedAt;

    @Builder
    public ArticleView(UUID userId, UUID articleId) {
        this.userId = userId;
        this.articleId = articleId;
        this.viewedAt = LocalDateTime.now();
    }

    public void updateViewedAt() {
        this.viewedAt = LocalDateTime.now();
    }
}
