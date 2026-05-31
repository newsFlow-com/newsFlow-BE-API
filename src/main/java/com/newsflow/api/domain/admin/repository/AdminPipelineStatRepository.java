package com.newsflow.api.domain.admin.repository;

import com.newsflow.api.entity.PipelineStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AdminPipelineStatRepository extends JpaRepository<PipelineStat, UUID> {

    @Query("""
            SELECT p FROM PipelineStat p
            ORDER BY p.startedAt DESC
            """)
    Optional<PipelineStat> findLatest(
            org.springframework.data.domain.Pageable pageable
    );
}