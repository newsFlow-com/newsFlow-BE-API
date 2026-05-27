package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trending_keywords",
        indexes = {
                @Index(name = "ix_tk_date_rank", columnList = "trend_date, rank"),
                @Index(name = "ix_tk_keyword_date", columnList = "keyword_id, trend_date"),
                @Index(name = "ix_tk_type", columnList = "trend_type"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendingKeyword {

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