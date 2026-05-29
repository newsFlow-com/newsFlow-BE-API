package com.newsflow.api.domain.trending.repository;

import com.newsflow.api.entity.TrendingKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TrendingKeywordRepository extends JpaRepository<TrendingKeyword, UUID> {

    @Query("""
            SELECT t FROM TrendingKeyword t
            JOIN FETCH t.keyword k
            WHERE t.trendDate = :date
              AND t.trendType = :type
            ORDER BY t.rank ASC
            """)
    List<TrendingKeyword> findByDateAndType(
            @Param("date") LocalDate date,
            @Param("type") String type,
            Pageable pageable
    );
}