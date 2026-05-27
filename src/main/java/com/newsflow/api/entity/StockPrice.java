package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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