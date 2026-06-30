package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "banners",
        indexes = {
                @Index(name = "ix_banners_position_active", columnList = "position, is_active"),
                @Index(name = "ix_banners_start_end", columnList = "start_at, end_at"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(name = "link_url", columnDefinition = "text")
    private String linkUrl;

    /** main_top | sidebar | article_bottom | popup */
    @Column(nullable = false, length = 30)
    private String position;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "click_count", nullable = false)
    private int clickCount = 0;

    @Column(name = "impression_count", nullable = false)
    private int impressionCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Builder
    public Banner(String title, String imageUrl, String linkUrl, String position,
                  int displayOrder, LocalDateTime startAt, LocalDateTime endAt, User createdBy) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.position = position;
        this.displayOrder = displayOrder;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdBy = createdBy;
    }

    public void update(String title, String imageUrl, String linkUrl, String position,
                       Integer displayOrder, Boolean isActive,
                       LocalDateTime startAt, LocalDateTime endAt) {
        if (title != null) this.title = title;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (linkUrl != null) this.linkUrl = linkUrl;
        if (position != null) this.position = position;
        if (displayOrder != null) this.displayOrder = displayOrder;
        if (isActive != null) this.isActive = isActive;
        if (startAt != null) this.startAt = startAt;
        if (endAt != null) this.endAt = endAt;
    }

    public void deactivate() { this.isActive = false; }
}
