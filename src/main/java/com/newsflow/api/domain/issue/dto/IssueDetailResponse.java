package com.newsflow.api.domain.issue.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.entity.Article;
import com.newsflow.api.entity.Issue;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueDetailResponse {

    private UUID id;
    private String title;
    private String categorySlug;
    private String categoryName;
    private int articleCount;
    private int sourceCount;
    private LocalDateTime firstPublishedAt;
    private LocalDateTime lastPublishedAt;
    private List<ArticleResponse> articles;

    public static IssueDetailResponse from(Issue issue, List<Article> articles) {
        return IssueDetailResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .categorySlug(issue.getCategory() != null ? issue.getCategory().getSlug() : null)
                .categoryName(issue.getCategory() != null ? issue.getCategory().getName() : null)
                .articleCount(issue.getArticleCount())
                .sourceCount(issue.getSourceCount())
                .firstPublishedAt(issue.getFirstPublishedAt())
                .lastPublishedAt(issue.getLastPublishedAt())
                .articles(articles.stream().map(ArticleResponse::from).toList())
                .build();
    }
}
