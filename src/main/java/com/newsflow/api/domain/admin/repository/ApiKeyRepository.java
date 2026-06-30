package com.newsflow.api.domain.admin.repository;

import com.newsflow.api.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    Optional<ApiKey> findByKeyHashAndIsActiveTrue(String keyHash);
}
