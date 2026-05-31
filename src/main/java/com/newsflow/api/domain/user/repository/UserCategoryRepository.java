package com.newsflow.api.domain.user.repository;

import com.newsflow.api.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCategoryRepository extends JpaRepository<UserCategory, UUID> {

    @Query("""
            SELECT uc FROM UserCategory uc
            JOIN FETCH uc.category c
            WHERE uc.user.id = :userId
            ORDER BY uc.preferenceWeight DESC
            """)
    List<UserCategory> findByUserId(@Param("userId") UUID userId);

    Optional<UserCategory> findByUserIdAndCategoryId(UUID userId, UUID categoryId);

    boolean existsByUserIdAndCategoryId(UUID userId, UUID categoryId);
}