package com.newsflow.api.domain.b2b.repository;

import com.newsflow.api.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface B2bStatsRepository extends JpaRepository<Article, UUID> {

    @Query(value = """
            SELECT
                to_char(a.published_at AT TIME ZONE 'Asia/Seoul', 'YYYY-MM-DD') AS stat_date,
                c.slug  AS category_slug,
                c.name  AS category_name,
                COUNT(a.id) AS article_count
            FROM articles a
            JOIN article_categories ac ON ac.article_id = a.id
            JOIN categories c          ON c.id = ac.category_id
            WHERE a.status = 'active'
              AND a.published_at >= :from
              AND a.published_at < :to
              AND (:category IS NULL OR c.slug = :category)
            GROUP BY to_char(a.published_at AT TIME ZONE 'Asia/Seoul', 'YYYY-MM-DD'), c.slug, c.name
            ORDER BY stat_date DESC, article_count DESC
            """, nativeQuery = true)
    List<Object[]> findDailyStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("category") String category
    );

    @Query(value = """
            SELECT
                to_char(a.published_at AT TIME ZONE 'Asia/Seoul', 'YYYY-MM-DD') AS stat_date,
                COALESCE(a.sentiment, 'neutral')                                  AS sentiment,
                COUNT(*)                                                           AS article_count
            FROM articles a
            LEFT JOIN article_categories ac ON ac.article_id = a.id
            LEFT JOIN categories c          ON c.id = ac.category_id
            WHERE a.status = 'active'
              AND a.published_at >= :from
              AND a.published_at < :to
              AND (:category IS NULL OR c.slug = :category)
            GROUP BY to_char(a.published_at AT TIME ZONE 'Asia/Seoul', 'YYYY-MM-DD'), a.sentiment
            ORDER BY stat_date DESC
            """, nativeQuery = true)
    List<Object[]> findSentimentStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("category") String category
    );

    @Query(value = """
            SELECT
                k.word            AS word,
                COUNT(tk.id)      AS mention_count,
                AVG(tk.rank)      AS avg_rank,
                MIN(tk.rank)      AS best_rank
            FROM trending_keywords tk
            JOIN keywords k ON k.id = tk.keyword_id
            WHERE tk.trend_date >= CAST(:from AS date)
              AND tk.trend_date <= CAST(:to AS date)
            GROUP BY k.id, k.word
            ORDER BY mention_count DESC, avg_rank ASC
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Object[]> findKeywordStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("limitCount") int limitCount
    );
}
