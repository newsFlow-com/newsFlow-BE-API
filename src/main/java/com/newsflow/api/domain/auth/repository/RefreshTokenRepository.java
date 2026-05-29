package com.newsflow.api.domain.auth.repository;

import com.newsflow.api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true) // 벌크 연산 후 영속성 컨텍스트 1차 캐시 미스매치 방지
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.user.id = :userId AND t.gate = :gate")
    void revokeAllByUserIdAndGate(@Param("userId") UUID userId, @Param("gate") String gate);
}