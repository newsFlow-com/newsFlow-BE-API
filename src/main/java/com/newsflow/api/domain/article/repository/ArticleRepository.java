package com.newsflow.api.domain.article.repository;

import com.newsflow.api.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

    /**
     * 카테고리 필터 없이 커서 기반 페이지네이션.
     * published_at DESC → id DESC 복합 정렬로 커서 안정성 보장.
     */
    @Query("""
            SELECT a FROM Article a
            WHERE a.status = 'active'
              AND (
                a.publishedAt < :cursorDate
                OR (a.publishedAt = :cursorDate AND a.id < :cursorId)
              )
            ORDER BY a.publishedAt DESC, a.id DESC
            """)
    List<Article> findByStatusActiveCursor(
            @Param("cursorDate") LocalDateTime cursorDate,
            @Param("cursorId") UUID cursorId,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 첫 페이지 (커서 없음).
     */
    @Query("""
            SELECT a FROM Article a
            WHERE a.status = 'active'
            ORDER BY a.publishedAt DESC, a.id DESC
            """)
    List<Article> findByStatusActiveFirst(
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 카테고리 슬러그 필터 + 커서 페이지네이션.
     */
    @Query("""
            SELECT DISTINCT a FROM Article a
            JOIN a.articleCategories ac
            JOIN ac.category c
            WHERE a.status = 'active'
              AND c.slug = :categorySlug
              AND (
                a.publishedAt < :cursorDate
                OR (a.publishedAt = :cursorDate AND a.id < :cursorId)
              )
            ORDER BY a.publishedAt DESC, a.id DESC
            """)
    List<Article> findByCategorySlugCursor(
            @Param("categorySlug") String categorySlug,
            @Param("cursorDate") LocalDateTime cursorDate,
            @Param("cursorId") UUID cursorId,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 카테고리 슬러그 필터 + 첫 페이지.
     */
    @Query("""
            SELECT DISTINCT a FROM Article a
            JOIN a.articleCategories ac
            JOIN ac.category c
            WHERE a.status = 'active'
              AND c.slug = :categorySlug
            ORDER BY a.publishedAt DESC, a.id DESC
            """)
    List<Article> findByCategorySlugFirst(
            @Param("categorySlug") String categorySlug,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 키워드 검색 (제목 + 요약 LIKE).
     */
    @Query("""
            SELECT a FROM Article a
            WHERE a.status = 'active'
              AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
              AND (
                a.publishedAt < :cursorDate
                OR (a.publishedAt = :cursorDate AND a.id < :cursorId)
              )
            ORDER BY a.publishedAt DESC, a.id DESC
            """)
    List<Article> searchByKeywordCursor(
            @Param("keyword") String keyword,
            @Param("cursorDate") LocalDateTime cursorDate,
            @Param("cursorId") UUID cursorId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("""
            SELECT a FROM Article a
            WHERE a.status = 'active'
              AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY a.publishedAt DESC, a.id DESC
            """)
    List<Article> searchByKeywordFirst(
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * 특정 날짜 범위의 기사 조회 (달력 뷰용).
     */
    @Query("""
            SELECT a FROM Article a
            WHERE a.status = 'active'
              AND a.publishedAt >= :from
              AND a.publishedAt < :to
            ORDER BY a.publishedAt DESC
            """)
    List<Article> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            org.springframework.data.domain.Pageable pageable
    );
}