package com.newsflow.api.domain.search.dto;

import com.newsflow.api.entity.ArticleDocument;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SearchArticleResponse {

    private String id;
    private String title;
    private String summary;
    private String aiSummary;
    private String thumbnailUrl;
    private String originalUrl;
    private List<String> categories;
    private List<String> keywords;
    private String publishedAt;
    private float score;
    private Map<String, List<String>> highlights;

    public static SearchArticleResponse from(ArticleDocument doc,
                                             float score,
                                             Map<String, List<String>> highlights) {
        return SearchArticleResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .summary(doc.getSummary())
                .aiSummary(doc.getAiSummary())
                .thumbnailUrl(doc.getThumbnailUrl())
                .originalUrl(doc.getOriginalUrl())
                .categories(doc.getCategories())
                .keywords(doc.getKeywords())
                .publishedAt(doc.getPublishedAt())
                .score(score)
                .highlights(highlights)
                .build();
    }
}
