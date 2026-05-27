package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// ── ArticleCategory ───────────────────────────────────────────────
@Entity
@Table(name = "article_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_ac_article_category",
                columnNames = {"article_id", "category_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ArticleCategory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** rule | ai | manual */
    @Column(name = "classified_by", nullable = false, length = 20)
    private String classifiedBy = "rule";

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public ArticleCategory(Article article, Category category,
                           String classifiedBy, Double confidenceScore) {
        this.article = article;
        this.category = category;
        this.classifiedBy = classifiedBy != null ? classifiedBy : "rule";
        this.confidenceScore = confidenceScore;
        this.createdAt = LocalDateTime.now();
    }
}