package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions",
        indexes = {
                @Index(name = "ix_sub_user_type_value",
                        columnList = "user_id, subscription_type, value",
                        unique = true),
                @Index(name = "ix_sub_type_value_active",
                        columnList = "subscription_type, value, is_active"),
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** keyword | category */
    @Column(name = "subscription_type", nullable = false, length = 20)
    private String subscriptionType;

    /** 키워드 단어 또는 카테고리 slug */
    @Column(nullable = false, length = 100)
    private String value;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public Subscription(User user, String subscriptionType, String value) {
        this.user = user;
        this.subscriptionType = subscriptionType;
        this.value = value;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public void deactivate() { this.isActive = false; }
}
