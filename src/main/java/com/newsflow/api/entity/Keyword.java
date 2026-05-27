package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ── Keyword ───────────────────────────────────────────────────────
@Entity
@Table(name = "keywords",
        indexes = {
                @Index(name = "ix_keywords_normalized", columnList = "word_normalized"),
                @Index(name = "ix_keywords_language", columnList = "language_code"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Keyword {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String word;

    @Column(name = "word_normalized", length = 100)
    private String wordNormalized;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode = "ko";

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── ArticleKeyword ────────────────────────────────────────────────
@Entity
@Table(name = "article_keywords",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_ak_article_keyword",
                columnNames = {"article_id", "keyword_id"}),
        indexes = @Index(name = "ix_ak_relevance", columnList = "relevance_score"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ArticleKeyword {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Column(name = "relevance_score")
    private Double relevanceScore;

    /** tfidf | ai | rule */
    @Column(name = "extracted_by", nullable = false, length = 20)
    private String extractedBy = "tfidf";

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}

// ── TrendingKeyword ───────────────────────────────────────────────
@Entity
@Table(name = "trending_keywords",
        indexes = {
                @Index(name = "ix_tk_date_rank", columnList = "trend_date, rank"),
                @Index(name = "ix_tk_keyword_date", columnList = "keyword_id, trend_date"),
                @Index(name = "ix_tk_type", columnList = "trend_type"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TrendingKeyword {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Column(name = "trend_date", nullable = false)
    private LocalDate trendDate;

    /** daily | realtime */
    @Column(name = "trend_type", nullable = false, length = 20)
    private String trendType = "daily";

    @Column(nullable = false)
    private int rank;

    @Column(name = "search_volume_index")
    private Double searchVolumeIndex;

    /** pytrends | internal */
    @Column(nullable = false, length = 20)
    private String source = "pytrends";

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}