package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

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