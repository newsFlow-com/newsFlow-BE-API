package com.newsflow.api.domain.category.repository;

import com.newsflow.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {


    @Query("""
            SELECT DISTINCT c FROM Category c
            LEFT JOIN FETCH c.children ch
            WHERE c.isActive = true 
              AND c.parent IS NULL
            ORDER BY c.displayOrder ASC, ch.displayOrder ASC
            """)
    List<Category> findAllRootCategoriesWithChildren();

    Optional<Category> findBySlug(String slug);
}