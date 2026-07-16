package com.newsflow.api.domain.source.repository;

import com.newsflow.api.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SourceRepository extends JpaRepository<Source, UUID> {

    List<Source> findByIsActiveTrueOrderByNameAsc();
}
