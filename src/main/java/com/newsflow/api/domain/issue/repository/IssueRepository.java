package com.newsflow.api.domain.issue.repository;

import com.newsflow.api.entity.Issue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {

    @Query("""
            SELECT i FROM Issue i
            WHERE i.status = 'active'
            ORDER BY i.lastPublishedAt DESC
            """)
    List<Issue> findActiveOrderByLatest(Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            WHERE i.status = 'active'
            ORDER BY i.articleCount DESC
            """)
    List<Issue> findActiveOrderByArticleCount(Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            JOIN i.category c
            WHERE i.status = 'active' AND c.slug = :categorySlug
            ORDER BY i.lastPublishedAt DESC
            """)
    List<Issue> findActiveByCategoryOrderByLatest(
            @Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            JOIN i.category c
            WHERE i.status = 'active' AND c.slug = :categorySlug
            ORDER BY i.articleCount DESC
            """)
    List<Issue> findActiveByCategoryOrderByArticleCount(
            @Param("categorySlug") String categorySlug, Pageable pageable);
}
