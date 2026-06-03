package com.newsflow.api.domain.stock.repository;

import com.newsflow.api.entity.ArticleStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ArticleStockRepository extends JpaRepository<ArticleStock, UUID> {

    /**
     * 기사에 연관된 종목 목록 (mention_score 내림차순).
     */
    @Query("""
            SELECT a FROM ArticleStock a
            JOIN FETCH a.stock s
            WHERE a.article.id = :articleId
              AND s.isActive = true
            ORDER BY a.mentionScore DESC
            """)
    List<ArticleStock> findByArticleId(@Param("articleId") UUID articleId);

    /**
     * 특정 종목이 언급된 기사 목록 (최근순).
     */
    @Query("""
            SELECT a FROM ArticleStock a
            JOIN FETCH a.article ar
            WHERE a.stock.id = :stockId
              AND ar.status = 'active'
            ORDER BY ar.publishedAt DESC
            """)
    List<ArticleStock> findByStockId(
            @Param("stockId") UUID stockId,
            org.springframework.data.domain.Pageable pageable
    );
}