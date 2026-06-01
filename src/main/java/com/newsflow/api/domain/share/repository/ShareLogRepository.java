package com.newsflow.api.domain.share.repository;

import com.newsflow.api.entity.ShareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ShareLogRepository extends JpaRepository<ShareLog, UUID> {

    /**
     * 사용자별 공유 이력 조회 (최근순).
     */
    @Query("""
            SELECT s FROM ShareLog s
            WHERE s.user.id = :userId
            ORDER BY s.createdAt DESC
            """)
    List<ShareLog> findByUserId(
            @Param("userId") UUID userId,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 특정 타겟(기사/차트/주식)의 공유 수 집계.
     */
    long countByTargetTypeAndTargetId(String targetType, UUID targetId);

    /**
     * 채널별 공유 통계 (관리자 대시보드용).
     */
    @Query("""
            SELECT s.channel, COUNT(s)
            FROM ShareLog s
            GROUP BY s.channel
            ORDER BY COUNT(s) DESC
            """)
    List<Object[]> countGroupByChannel();
}