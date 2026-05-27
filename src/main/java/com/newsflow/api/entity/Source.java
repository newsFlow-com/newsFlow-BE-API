package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sources",
        indexes = @Index(name = "ix_sources_active_tier", columnList = "is_active, tier"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Source extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String domain;

    @Column(name = "feed_url", columnDefinition = "text")
    private String feedUrl;

    /** rss | api | crawl */
    @Column(name = "feed_type", nullable = false, length = 20)
    private String feedType;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "language_code", length = 10)
    private String languageCode;

    /** domestic | international */
    @Column(nullable = false, length = 20)
    private String tier = "domestic";

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_fetched_at")
    private LocalDateTime lastFetchedAt;
}