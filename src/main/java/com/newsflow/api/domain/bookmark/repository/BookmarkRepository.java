package com.newsflow.api.domain.bookmark.repository;

import com.newsflow.api.entity.Bookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    boolean existsByUserIdAndArticleId(UUID userId, UUID articleId);

    Optional<Bookmark> findByUserIdAndArticleId(UUID userId, UUID articleId);

    /**
     * 💡 [N+1 최적화 고도화]
     * ArticleResponse.from() 내부에서 참조하는 article.getSource(),
     * article.getArticleCategories() 및 category 구조까지 전부 한 번에 네트워크로 퍼옵니다.
     */
    @Query("""
            SELECT DISTINCT b FROM Bookmark b
            JOIN FETCH b.article a
            LEFT JOIN FETCH a.source s
            LEFT JOIN FETCH a.articleCategories ac
            LEFT JOIN FETCH ac.category c
            WHERE b.user.id = :userId
              AND a.status = 'active'
            ORDER BY b.createdAt DESC
            """)
    List<Bookmark> findByUserIdWithDetails(
            @Param("userId") UUID userId,
            Pageable pageable
    );
}