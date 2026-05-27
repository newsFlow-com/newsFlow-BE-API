package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens",
        indexes = @Index(name = "ix_refresh_user_gate", columnList = "user_id, gate"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 512)
    private String tokenHash;

    /**
     * user  → 일반 사용자 게이트 토큰
     * admin → 관리자 게이트 토큰 (별도 시크릿으로 서명)
     */
    @Column(nullable = false, length = 10)
    private String gate = "user";

    @Column(name = "is_revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "timestamp with time zone default now()")
    private LocalDateTime createdAt;

    @Builder
    public RefreshToken(User user, String tokenHash, String gate,
                        LocalDateTime expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.gate = gate != null ? gate : "user";
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    public void revoke() { this.revoked = true; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}