package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "article_keywords",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_ak_article_keyword",
                columnNames = {"article_id", "keyword_id"}),
        indexes = @Index(name = "ix_ak_relevance", columnList = "relevance_score"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleKeyword {

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