package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

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