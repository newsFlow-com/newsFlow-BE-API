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

    /** 발행일 기준 가장 가까운 거래일의 주가 등락률(%). 인과관계가 아닌 동조화 참고 지표. */
    @Column(name = "price_change_publish_day")
    private Double priceChangePublishDay;

    /** 발행일 종가 → 3거래일 후 종가까지의 누적 변동률(%) */
    @Column(name = "price_change_3d")
    private Double priceChange3d;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}