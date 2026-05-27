package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_user_stats",
        indexes = @Index(name = "ix_dus_date", columnList = "stat_date", unique = true))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyUserStat {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "stat_date", nullable = false, unique = true)
    private LocalDate statDate;

    @Column(name = "total_users", nullable = false)
    private int totalUsers;

    @Column(name = "active_users", nullable = false)
    private int activeUsers;

    @Column(name = "new_users", nullable = false)
    private int newUsers;

    @Column(name = "suspended_users", nullable = false)
    private int suspendedUsers;

    @Column(name = "deleted_users", nullable = false)
    private int deletedUsers;

    @Column(name = "kakao_signup_count", nullable = false)
    private int kakaoSignupCount;

    @Column(name = "google_signup_count", nullable = false)
    private int googleSignupCount;

    @Column(name = "churn_rate")
    private Double churnRate;

    @Column(name = "mau")
    private Integer mau;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;
}