package com.newsflow.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"socialAccounts", "refreshTokens", "bookmarks", "userCategories"})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    /**
     * user  → 일반 사용자 게이트
     * admin → 관리자 게이트
     */
    @Column(nullable = false, length = 20)
    private String role = "user";

    /**
     * active | suspended | deleted
     */
    @Column(nullable = false, length = 20)
    private String status = "active";

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ── Relations ────────────────────────────────────────────────
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCategory> userCategories = new ArrayList<>();

    // ── 생성자 ────────────────────────────────────────────────────
    @Builder
    public User(String email, String passwordHash, String nickname,
                String profileImageUrl, String role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role != null ? role : "user";
    }

    // ── 도메인 메서드 ─────────────────────────────────────────────
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null) this.nickname = nickname;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

    public void suspend() { this.status = "suspended"; }
    public void activate() { this.status = "active"; }
    public void delete() { this.status = "deleted"; }
    public void verifyEmail() { this.emailVerified = true; }

    public boolean isAdmin() { return "admin".equals(this.role); }
    public boolean isActive() { return "active".equals(this.status); }
}