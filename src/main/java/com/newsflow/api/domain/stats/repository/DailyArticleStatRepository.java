package com.newsflow.api.domain.stats.repository;

import com.newsflow.api.entity.DailyArticleStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyArticleStatRepository extends JpaRepository<DailyArticleStat, UUID> {

    @Query("""
            SELECT d.statDate, COUNT(d), SUM(d.viewCount), MAX(d.trendingScore)
            FROM DailyArticleStat d
            WHERE d.statDate BETWEEN :from AND :to
            GROUP BY d.statDate
            ORDER BY d.statDate
            """)
    List<Object[]> findDailySummary(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
            SELECT DISTINCT d FROM DailyArticleStat d
            JOIN FETCH d.article a
            LEFT JOIN FETCH a.source s
            LEFT JOIN FETCH a.articleCategories ac
            LEFT JOIN FETCH ac.category c
            WHERE d.statDate = :date
              AND a.status = 'active'
            ORDER BY d.trendingScore DESC
            """)
    List<DailyArticleStat> findByStatDateWithDetails(
            @Param("date") LocalDate date,
            Pageable pageable
    );


    @Query("""
            SELECT DISTINCT d FROM DailyArticleStat d
            JOIN FETCH d.article a
            LEFT JOIN FETCH a.source s
            LEFT JOIN FETCH a.articleCategories ac
            LEFT JOIN FETCH ac.category c
            WHERE d.statDate BETWEEN :start AND :end
              AND a.status = 'active'
            ORDER BY d.trendingScore DESC
            """)
    List<DailyArticleStat> findTopByMonthWithDetails(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );
}