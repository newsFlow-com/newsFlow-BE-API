package com.newsflow.api.domain.log.repository;

import com.newsflow.api.entity.ApiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, UUID> {
}
