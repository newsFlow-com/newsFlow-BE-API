package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "article_stocks",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_as_article_stock",
                columnNames = {"article_id", "stock_id"}),
        indexes = @Index(name = "ix_as_stock", columnList = "stock_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleStock {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "mention_score")
    private Double mentionScore;

    /** ai | rule */
    @Column(name = "linked_by", nullable = false, length = 20)
    private String linkedBy = "rule";

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}