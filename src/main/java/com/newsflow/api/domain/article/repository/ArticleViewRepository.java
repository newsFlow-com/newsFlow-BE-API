package com.newsflow.api.domain.article.repository;

import com.newsflow.api.entity.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {
    Optional<ArticleView> findByUserIdAndArticleId(UUID userId, UUID articleId);
}
