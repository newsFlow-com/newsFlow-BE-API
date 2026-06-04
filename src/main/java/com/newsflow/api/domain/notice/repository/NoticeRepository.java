package com.newsflow.api.domain.notice.repository;

import com.newsflow.api.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    /**
     * 사용자 게이트 공지사항 목록.
     * 활성 + 노출 기간 유효 + 고정 우선 + 최신순.
     */
    @Query("""
            SELECT n FROM Notice n
            WHERE n.isActive = true
              AND n.targetGate IN ('user', 'all')
              AND (n.publishedAt IS NULL OR n.publishedAt <= :now)
              AND (n.expiredAt IS NULL OR n.expiredAt > :now)
            ORDER BY n.isPinned DESC, n.createdAt DESC
            """)
    List<Notice> findActiveForUser(
            @Param("now") LocalDateTime now,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 관리자 게이트 공지사항 목록 (전체 포함).
     */
    @Query("""
            SELECT n FROM Notice n
            WHERE n.isActive = true
              AND n.targetGate IN ('admin', 'all')
              AND (n.publishedAt IS NULL OR n.publishedAt <= :now)
              AND (n.expiredAt IS NULL OR n.expiredAt > :now)
            ORDER BY n.isPinned DESC, n.createdAt DESC
            """)
    List<Notice> findActiveForAdmin(
            @Param("now") LocalDateTime now,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 고정 공지사항만 조회 (메인 화면 상단 노출용).
     */
    @Query("""
            SELECT n FROM Notice n
            WHERE n.isActive = true
              AND n.isPinned = true
              AND n.targetGate IN ('user', 'all')
              AND (n.publishedAt IS NULL OR n.publishedAt <= :now)
              AND (n.expiredAt IS NULL OR n.expiredAt > :now)
            ORDER BY n.createdAt DESC
            """)
    List<Notice> findPinnedForUser(@Param("now") LocalDateTime now);
}