package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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