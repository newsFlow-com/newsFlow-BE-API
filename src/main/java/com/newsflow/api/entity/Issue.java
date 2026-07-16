package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "issues",
        indexes = {
                @Index(name = "ix_issues_category_last_published", columnList = "category_id, last_published_at"),
                @Index(name = "ix_issues_status", columnList = "status"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issue extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_article_id")
    private Article representativeArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "article_count", nullable = false)
    private int articleCount = 1;

    @Column(name = "source_count", nullable = false)
    private int sourceCount = 1;

    @Column(name = "first_published_at")
    private LocalDateTime firstPublishedAt;

    @Column(name = "last_published_at")
    private LocalDateTime lastPublishedAt;

    /** active | archived */
    @Column(nullable = false, length = 20)
    private String status = "active";

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueKeyword> issueKeywords = new ArrayList<>();
}
