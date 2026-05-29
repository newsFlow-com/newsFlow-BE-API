package com.newsflow.api.domain.article.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newsflow.api.entity.Article;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleDetailResponse {

    private UUID id;
    private String title;
    private String summary;
    private String content;
    private String thumbnailUrl;
    private String originalUrl;
    private String author;
    private String languageCode;
    private String status;
    private LocalDateTime publishedAt;
    private LocalDateTime collectedAt;
    private int viewCount;

    private String sourceName;
    private String sourceDomain;

    private List<ArticleResponse.CategoryInfo> categories;
    private List<KeywordInfo> keywords;

    @Getter
    @Builder
    public static class KeywordInfo {
        private String word;
        private Double relevanceScore;
    }

    public static ArticleDetailResponse from(Article article) {
        List<ArticleResponse.CategoryInfo> categories = article.getArticleCategories().stream()
                .map(ac -> ArticleResponse.CategoryInfo.builder()
                        .slug(ac.getCategory().getSlug())
                        .name(ac.getCategory().getName())
                        .build())
                .toList();

        List<KeywordInfo> keywords = article.getArticleKeywords().stream()
                .map(ak -> KeywordInfo.builder()
                        .word(ak.getKeyword().getWord())
                        .relevanceScore(ak.getRelevanceScore())
                        .build())
                .toList();

        return ArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .content(article.getContent())
                .thumbnailUrl(article.getThumbnailUrl())
                .originalUrl(article.getOriginalUrl())
                .author(article.getAuthor())
                .languageCode(article.getLanguageCode())
                .status(article.getStatus())
                .publishedAt(article.getPublishedAt())
                .collectedAt(article.getCollectedAt())
                .viewCount(article.getViewCount())
                .sourceName(article.getSource() != null ? article.getSource().getName() : null)
                .sourceDomain(article.getSource() != null ? article.getSource().getDomain() : null)
                .categories(categories)
                .keywords(keywords)
                .build();
    }
}