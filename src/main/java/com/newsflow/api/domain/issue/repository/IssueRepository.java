package com.newsflow.api.domain.issue.repository;

import com.newsflow.api.entity.Issue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {

    @Query("""
            SELECT i FROM Issue i
            WHERE i.status = 'active'
            ORDER BY i.lastPublishedAt DESC
            """)
    List<Issue> findActiveOrderByLatest(Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            WHERE i.status = 'active'
            ORDER BY i.articleCount DESC
            """)
    List<Issue> findActiveOrderByArticleCount(Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            JOIN i.category c
            WHERE i.status = 'active' AND c.slug = :categorySlug
            ORDER BY i.lastPublishedAt DESC
            """)
    List<Issue> findActiveByCategoryOrderByLatest(
            @Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            JOIN i.category c
            WHERE i.status = 'active' AND c.slug = :categorySlug
            ORDER BY i.articleCount DESC
            """)
    List<Issue> findActiveByCategoryOrderByArticleCount(
            @Param("categorySlug") String categorySlug, Pageable pageable);

    /**
     * 속보 후보 — 최근 시간 내 갱신되고 매체 minSourceCount곳 이상이 다룬 이슈.
     * 속도 점수(source_count / 경과시간) 계산·정렬은 서비스 레이어에서 수행한다.
     */
    @Query("""
            SELECT i FROM Issue i
            WHERE i.status = 'active'
              AND i.lastPublishedAt >= :since
              AND i.sourceCount >= :minSourceCount
            ORDER BY i.lastPublishedAt DESC
            """)
    List<Issue> findBreakingCandidates(
            @Param("since") LocalDateTime since,
            @Param("minSourceCount") int minSourceCount,
            Pageable pageable
    );
}
