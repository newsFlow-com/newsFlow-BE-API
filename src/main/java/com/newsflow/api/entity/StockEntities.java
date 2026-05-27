package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ── Stock ─────────────────────────────────────────────────────────
@Entity
@Table(name = "stocks",
        indexes = {
                @Index(name = "ix_stocks_market_active", columnList = "market, is_active"),
                @Index(name = "ix_stocks_sector", columnList = "sector"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String ticker;

    @Column(nullable = false, length = 100)
    private String name;

    /** KOSPI | KOSDAQ | NYSE | NASDAQ */
    @Column(nullable = false, length = 20)
    private String market;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode = "KR";

    @Column(length = 100)
    private String sector;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}

// ── ArticleStock ──────────────────────────────────────────────────
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

// ── StockPrice ────────────────────────────────────────────────────
@Entity
@Table(name = "stock_prices",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_sp_stock_date",
                columnNames = {"stock_id", "price_date"}),
        indexes = @Index(name = "ix_sp_date", columnList = "price_date"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPrice {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "price_date", nullable = false)
    private LocalDate priceDate;

    @Column(name = "open_price", precision = 18, scale = 4)
    private BigDecimal openPrice;

    @Column(name = "close_price", precision = 18, scale = 4)
    private BigDecimal closePrice;

    @Column(name = "high_price", precision = 18, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 18, scale = 4)
    private BigDecimal lowPrice;

    @Column(columnDefinition = "bigint")
    private Long volume;

    @Column(name = "change_rate", precision = 8, scale = 4)
    private BigDecimal changeRate;

    @Column(name = "fetched_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime fetchedAt;
}