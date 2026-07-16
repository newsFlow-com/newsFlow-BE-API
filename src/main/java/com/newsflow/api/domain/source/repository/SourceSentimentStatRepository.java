package com.newsflow.api.domain.source.repository;

import com.newsflow.api.entity.SourceSentimentStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SourceSentimentStatRepository extends JpaRepository<SourceSentimentStat, UUID> {

    @Query(value = """
            SELECT
                c.slug AS category_slug,
                c.name AS category_name,
                SUM(s.positive_count) AS positive,
                SUM(s.negative_count) AS negative,
                SUM(s.neutral_count)  AS neutral
            FROM source_sentiment_stats s
            LEFT JOIN categories c ON c.id = s.category_id
            WHERE s.source_id = :sourceId
              AND s.stat_date >= :from
              AND s.stat_date <= :to
            GROUP BY c.slug, c.name
            ORDER BY (SUM(s.positive_count) + SUM(s.negative_count) + SUM(s.neutral_count)) DESC
            """, nativeQuery = true)
    List<Object[]> findCategoryBreakdown(
            @Param("sourceId") UUID sourceId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
