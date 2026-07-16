package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "source_sentiment_stats",
        indexes = @Index(name = "ix_sss_date", columnList = "stat_date"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SourceSentimentStat extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "positive_count", nullable = false)
    private int positiveCount;

    @Column(name = "negative_count", nullable = false)
    private int negativeCount;

    @Column(name = "neutral_count", nullable = false)
    private int neutralCount;
}
