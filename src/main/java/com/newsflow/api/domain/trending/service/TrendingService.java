package com.newsflow.api.domain.trending.service;

import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.trending.dto.TrendingArticleResponse;
import com.newsflow.api.domain.trending.dto.TrendingKeywordResponse;
import com.newsflow.api.domain.trending.repository.TrendingKeywordRepository;
import com.newsflow.api.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendingService {

    private static final String TRENDING_ARTICLES_KEY = "trending:articles";

    private final TrendingKeywordRepository trendingKeywordRepository;
    private final ArticleRepository articleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public List<TrendingArticleResponse> getTrendingArticles(int size) {
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(TRENDING_ARTICLES_KEY, 0, size - 1);

        if (tuples == null || tuples.isEmpty()) {
            return getDbTrendingArticles(size);
        }

        // 1. Redis에서 내려온 순서대로 튜플 정보를 맵에 임시 캐싱 (ID 유효성 검증 포함)
        List<ZSetOperations.TypedTuple<String>> validTuples = tuples.stream()
                .filter(t -> t.getValue() != null)
                .toList();

        if (validTuples.isEmpty()) {
            return getDbTrendingArticles(size);
        }

        List<UUID> articleIds = validTuples.stream()
                .map(t -> UUID.fromString(t.getValue()))
                .toList();

        // 2. [N+1 쿼리 지옥 해결]: 단건 findById를 루프에서 전면 배제하고 IN 절로 단 1방에 조회
        List<Article> articles = articleRepository.findAllById(articleIds);

        // 3. 빠른 매핑을 위해 기사 리스트를 ID 기준 Map 변환
        Map<UUID, Article> articleMap = articles.stream()
                .filter(a -> "active".equals(a.getStatus()))
                .collect(Collectors.toMap(Article::getId, Function.identity()));

        // 4. [랭킹 순서 보장]: DB IN 절로 섞인 순서를 Redis 원본 튜플 랭킹 타임라인대로 다시 조립
        int[] rank = {1};
        return validTuples.stream()
                .map(t -> {
                    UUID id = UUID.fromString(t.getValue());
                    Article article = articleMap.get(id);
                    if (article == null) return null;

                    return TrendingArticleResponse.builder()
                            .rank(rank[0]++)
                            .article(ArticleResponse.from(article))
                            .viewCount(t.getScore() != null ? t.getScore().longValue() : 0L)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<TrendingArticleResponse> getDbTrendingArticles(int size) {
        int[] rank = {1};
        // 최상위 엔티티 리스트를 가져와 안전하게 변환
        return articleRepository.findByStatusActiveFirst(PageRequest.of(0, size))
                .stream()
                .map(a -> TrendingArticleResponse.builder()
                        .rank(rank[0]++)
                        .article(ArticleResponse.from(a))
                        .viewCount(a.getViewCount())
                        .build())
                .toList();
    }

     //급상승 검색 키워드
    public List<TrendingKeywordResponse> getTrendingKeywords(int size) {
        LocalDate today = LocalDate.now();
        return trendingKeywordRepository
                .findByDateAndType(today, "daily", PageRequest.of(0, size))
                .stream()
                .map(TrendingKeywordResponse::from)
                .toList();
    }
}