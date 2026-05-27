package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "ix_uc_user_category",
                columnNames = {"user_id", "category_id"}),
        indexes = @Index(name = "ix_uc_user", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "preference_weight", nullable = false)
    private int preferenceWeight = 5;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public UserCategory(User user, Category category, int preferenceWeight) {
        this.user = user;
        this.category = category;
        this.preferenceWeight = preferenceWeight;
        this.createdAt = LocalDateTime.now();
    }

    public void updateWeight(int weight) {
        this.preferenceWeight = weight;
    }
}