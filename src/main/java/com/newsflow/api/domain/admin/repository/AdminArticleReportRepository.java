package com.newsflow.api.domain.admin.repository;

import com.newsflow.api.entity.ArticleReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminArticleReportRepository extends JpaRepository<ArticleReport, UUID> {

    long countByStatus(String status);

    @Query("""
            SELECT r FROM ArticleReport r
            JOIN FETCH r.article a
            LEFT JOIN FETCH r.reporter u
            WHERE r.status = :status
            ORDER BY r.createdAt DESC
            """)
    List<ArticleReport> findByStatus(
            @Param("status") String status,
            org.springframework.data.domain.Pageable pageable
    );
}