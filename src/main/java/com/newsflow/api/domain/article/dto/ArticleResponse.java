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
public class ArticleResponse {

    private UUID id;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private String originalUrl;
    private String author;
    private String languageCode;
    private LocalDateTime publishedAt;
    private LocalDateTime collectedAt;
    private int viewCount;

    private String sourceName;
    private String sourceDomain;

    private List<CategoryInfo> categories;

    @Getter
    @Builder
    public static class CategoryInfo {
        private String slug;
        private String name;
    }

    public static ArticleResponse from(Article article) {
        List<CategoryInfo> categories = article.getArticleCategories().stream()
                .map(ac -> CategoryInfo.builder()
                        .slug(ac.getCategory().getSlug())
                        .name(ac.getCategory().getName())
                        .build())
                .toList();

        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .thumbnailUrl(article.getThumbnailUrl())
                .originalUrl(article.getOriginalUrl())
                .author(article.getAuthor())
                .languageCode(article.getLanguageCode())
                .publishedAt(article.getPublishedAt())
                .collectedAt(article.getCollectedAt())
                .viewCount(article.getViewCount())
                .sourceName(article.getSource() != null ? article.getSource().getName() : null)
                .sourceDomain(article.getSource() != null ? article.getSource().getDomain() : null)
                .categories(categories)
                .build();
    }
}