package com.newsflow.api.domain.admin.repository;

import com.newsflow.api.entity.ContentQualityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ContentQualityLogRepository extends JpaRepository<ContentQualityLog, UUID> {

    @Query("SELECT l FROM ContentQualityLog l " +
            "WHERE (:checkType IS NULL OR l.checkType = :checkType) " +
            "AND (:isCorrect IS NULL OR l.isCorrect = :isCorrect) " +
            "ORDER BY l.createdAt DESC")
    Page<ContentQualityLog> findByFilters(
            @Param("checkType") String checkType,
            @Param("isCorrect") Boolean isCorrect,
            Pageable pageable
    );
}
