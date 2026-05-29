package com.newsflow.api.domain.article.service;

import com.newsflow.api.common.dto.CursorPageResponse;
import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.article.dto.ArticleDetailResponse;
import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private static final int MAX_SIZE = 50;
    private static final String VIEW_COUNT_KEY = "article:view:";
    private static final DateTimeFormatter CURSOR_DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ArticleRepository articleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public CursorPageResponse<ArticleResponse> getArticles(
            String categorySlug,
            String keyword,
            String cursor,
            int size) {

        size = Math.min(size, MAX_SIZE);
        var pageable = PageRequest.of(0, size + 1);

        List<Article> articles;

        if (cursor == null) {
            if (keyword != null && !keyword.isBlank()) {
                articles = articleRepository.searchByKeywordFirst(keyword.trim(), pageable);
            } else if (categorySlug != null && !categorySlug.isBlank()) {
                articles = articleRepository.findByCategorySlugFirst(categorySlug, pageable);
            } else {
                articles = articleRepository.findByStatusActiveFirst(pageable);
            }
        } else {
            CursorValue cv = parseCursor(cursor);
            if (keyword != null && !keyword.isBlank()) {
                articles = articleRepository.searchByKeywordCursor(
                        keyword.trim(), cv.publishedAt, cv.id, pageable);
            } else if (categorySlug != null && !categorySlug.isBlank()) {
                articles = articleRepository.findByCategorySlugCursor(
                        categorySlug, cv.publishedAt, cv.id, pageable);
            } else {
                articles = articleRepository.findByStatusActiveCursor(
                        cv.publishedAt, cv.id, pageable);
            }
        }

        // 최적화: 커서 추출 시 DB 재조회(findById)를 완전히 걷어내고 DTO 메모리 데이터 사용
        return CursorPageResponse.of(
                articles.stream().map(ArticleResponse::from).toList(),
                size,
                dto -> buildCursor(dto.getPublishedAt(), dto.getId())
        );
    }

    @Transactional
    public ArticleDetailResponse getArticle(UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .filter(Article::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        incrementViewCount(articleId);

        return ArticleDetailResponse.from(article);
    }

    private void incrementViewCount(UUID articleId) {
        try {
            redisTemplate.opsForValue().increment(VIEW_COUNT_KEY + articleId);
        } catch (Exception e) {
            log.warn("Redis 조회수 증가 실패: articleId={}", articleId);
        }
    }

    private String buildCursor(LocalDateTime publishedAt, UUID id) {
        String dateStr = publishedAt != null
                ? publishedAt.format(CURSOR_DATE_FMT)
                : LocalDateTime.now().format(CURSOR_DATE_FMT);
        return dateStr + "|" + id;
    }

    private CursorValue parseCursor(String cursor) {
        try {
            String[] parts = cursor.split("\\|");
            return new CursorValue(
                    LocalDateTime.parse(parts[0], CURSOR_DATE_FMT),
                    UUID.fromString(parts[1])
            );
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "올바르지 않은 커서 형식입니다.");
        }
    }

    private record CursorValue(LocalDateTime publishedAt, UUID id) {}
}