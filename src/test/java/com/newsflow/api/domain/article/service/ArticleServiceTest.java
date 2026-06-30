package com.newsflow.api.domain.article.service;

import com.newsflow.api.common.client.AiServerClient;
import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.article.repository.ArticleViewRepository;
import com.newsflow.api.entity.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks ArticleService articleService;
    @Mock ArticleRepository articleRepository;
    @Mock ArticleViewRepository articleViewRepository;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock AiServerClient aiServerClient;

    @Test
    @DisplayName("기사 상세 조회 시 Redis INCR와 ZSet ZINCRBY 모두 호출된다")
    void getArticle_incrementsRedisCountAndZSet() {
        UUID articleId = UUID.randomUUID();
        Article article = mock(Article.class);
        when(article.isActive()).thenReturn(true);
        when(article.getArticleCategories()).thenReturn(List.of());
        when(article.getArticleKeywords()).thenReturn(List.of());

        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);

        articleService.getArticle(articleId);

        verify(valueOps).increment("article:view:" + articleId);
        verify(zSetOps).incrementScore("trending:articles", articleId.toString(), 1.0);
    }

    @Test
    @DisplayName("비활성 기사 조회 시 BusinessException 발생")
    void getArticle_throwsWhenArticleInactive() {
        UUID articleId = UUID.randomUUID();
        Article article = mock(Article.class);
        when(article.isActive()).thenReturn(false);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        assertThatThrownBy(() -> articleService.getArticle(articleId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 기사 조회 시 BusinessException 발생")
    void getArticle_throwsWhenNotFound() {
        UUID articleId = UUID.randomUUID();
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticle(articleId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Redis 장애 발생 시에도 기사 조회는 성공한다")
    void getArticle_succeedsEvenIfRedisFails() {
        UUID articleId = UUID.randomUUID();
        Article article = mock(Article.class);
        when(article.isActive()).thenReturn(true);
        when(article.getArticleCategories()).thenReturn(List.of());
        when(article.getArticleKeywords()).thenReturn(List.of());
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis 연결 실패"));

        articleService.getArticle(articleId);

        verify(articleRepository).findById(articleId);
    }

    @Test
    @DisplayName("로그인 사용자 — BE-AI 추천 결과로 기사 반환")
    void getRecommendedArticles_returnsAiRecommendations_whenLoggedIn() {
        UUID userId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        Article article = mock(Article.class);
        when(article.getId()).thenReturn(articleId);
        when(article.getArticleCategories()).thenReturn(List.of());

        when(aiServerClient.getRecommendedArticleIds(userId, 10))
                .thenReturn(List.of(articleId.toString()));
        when(articleRepository.findAllById(List.of(articleId)))
                .thenReturn(List.of(article));

        var result = articleService.getRecommendedArticles(userId, 10);

        assertThat(result).hasSize(1);
        verify(aiServerClient).getRecommendedArticleIds(userId, 10);
        verify(articleRepository, never()).findByStatusActiveFirst(any(Pageable.class));
    }

    @Test
    @DisplayName("비로그인 사용자 — 최신 기사 fallback 반환")
    void getRecommendedArticles_returnsFallback_whenNotLoggedIn() {
        Article article = mock(Article.class);
        when(article.getArticleCategories()).thenReturn(List.of());
        when(articleRepository.findByStatusActiveFirst(any(Pageable.class)))
                .thenReturn(List.of(article));

        var result = articleService.getRecommendedArticles(null, 10);

        assertThat(result).hasSize(1);
        verify(aiServerClient, never()).getRecommendedArticleIds(any(), anyInt());
    }

    @Test
    @DisplayName("BE-AI 응답 없을 때 최신 기사 fallback 반환")
    void getRecommendedArticles_returnsFallback_whenAiReturnsEmpty() {
        UUID userId = UUID.randomUUID();
        Article article = mock(Article.class);
        when(article.getArticleCategories()).thenReturn(List.of());

        when(aiServerClient.getRecommendedArticleIds(userId, 10)).thenReturn(List.of());
        when(articleRepository.findByStatusActiveFirst(any(Pageable.class)))
                .thenReturn(List.of(article));

        var result = articleService.getRecommendedArticles(userId, 10);

        assertThat(result).hasSize(1);
        verify(articleRepository).findByStatusActiveFirst(any(Pageable.class));
    }
}
