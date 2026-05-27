package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "articles",
        indexes = {
                @Index(name = "ix_articles_published", columnList = "published_at"),
                @Index(name = "ix_articles_source_published", columnList = "source_id, published_at"),
                @Index(name = "ix_articles_status", columnList = "status"),
                @Index(name = "ix_articles_language", columnList = "language_code"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    @Column(name = "original_url", nullable = false, unique = true, columnDefinition = "text")
    private String originalUrl;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "thumbnail_url", columnDefinition = "text")
    private String thumbnailUrl;

    @Column(length = 100)
    private String author;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode = "ko";

    /** active | hidden | deleted */
    @Column(nullable = false, length = 20)
    private String status = "active";

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "collected_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime collectedAt;

    // ── Relations ────────────────────────────────────────────────
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleCategory> articleCategories = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleKeyword> articleKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleStock> articleStocks = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    // ── 도메인 메서드 ─────────────────────────────────────────────
    public void hide() { this.status = "hidden"; }
    public void restore() { this.status = "active"; }
    public void delete() { this.status = "deleted"; }
    public void incrementViewCount() { this.viewCount++; }
    public boolean isActive() { return "active".equals(this.status); }
}