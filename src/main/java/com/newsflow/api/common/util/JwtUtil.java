package com.newsflow.api.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long userExpiration;
    private final long adminExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.user-expiration}") long userExpiration,
            @Value("${jwt.admin-expiration}") long adminExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.userExpiration = userExpiration;
        this.adminExpiration = adminExpiration;
    }

    // ── 토큰 생성 ─────────────────────────────────────────────────

    /**
     * Access Token 생성.
     * gate = "user" | "admin"
     */
    public String generateAccessToken(UUID userId, String role, String gate) {
        long expiration = "admin".equals(gate) ? adminExpiration : userExpiration;
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .claim("gate", gate)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * Refresh Token 생성 (페이로드 최소화).
     */
    public String generateRefreshToken(UUID userId, String gate) {
        long expiration = "admin".equals(gate) ? adminExpiration * 7 : userExpiration * 7;
        return Jwts.builder()
                .subject(userId.toString())
                .claim("gate", gate)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // ── 토큰 파싱 ─────────────────────────────────────────────────

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    public String extractGate(String token) {
        return parseToken(token).get("gate", String.class);
    }

    public String extractRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    // ── 토큰 검증 ─────────────────────────────────────────────────

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.debug("JWT invalid: {}", e.getMessage());
        }
        return false;
    }

    public boolean isExpired(String token) {
        try {
            parseToken(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 게이트 일치 여부 검증.
     * 관리자 게이트 API 에 일반 사용자 토큰으로 접근 시 차단.
     */
    public boolean validateGate(String token, String requiredGate) {
        try {
            String tokenGate = extractGate(token);
            return requiredGate.equals(tokenGate);
        } catch (JwtException e) {
            return false;
        }
    }
}