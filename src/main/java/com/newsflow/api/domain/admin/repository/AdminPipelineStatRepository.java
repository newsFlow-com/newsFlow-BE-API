package com.newsflow.api.domain.admin.repository;

import com.newsflow.api.entity.PipelineStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AdminPipelineStatRepository extends JpaRepository<PipelineStat, UUID> {

    @Query("""
            SELECT p FROM PipelineStat p
            ORDER BY p.startedAt DESC
            """)
    List<PipelineStat> findLatest(Pageable pageable);
}