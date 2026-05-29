package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "article_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_ac_article_category",
                columnNames = {"article_id", "category_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleCategory { // 💡 public class로 변경하여 타 패키지(DTO) 접근 허용

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // 💡 Deprecated된 구형 Generator 제거 및 표준 UUID 생성 적용
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