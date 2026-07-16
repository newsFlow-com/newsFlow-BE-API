package com.newsflow.api.domain.issue.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newsflow.api.entity.Issue;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueResponse {

    private UUID id;
    private String title;
    private String categorySlug;
    private String categoryName;
    private int articleCount;
    private int sourceCount;
    private LocalDateTime firstPublishedAt;
    private LocalDateTime lastPublishedAt;
    private UUID representativeArticleId;
    private String representativeThumbnailUrl;

    /** 속보 속도 점수 (source_count / 최초보도 이후 경과시간). /issues/breaking 에서만 채워진다. */
    private Double breakingScore;

    public static IssueResponse from(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .categorySlug(issue.getCategory() != null ? issue.getCategory().getSlug() : null)
                .categoryName(issue.getCategory() != null ? issue.getCategory().getName() : null)
                .articleCount(issue.getArticleCount())
                .sourceCount(issue.getSourceCount())
                .firstPublishedAt(issue.getFirstPublishedAt())
                .lastPublishedAt(issue.getLastPublishedAt())
                .representativeArticleId(
                        issue.getRepresentativeArticle() != null
                                ? issue.getRepresentativeArticle().getId() : null)
                .representativeThumbnailUrl(
                        issue.getRepresentativeArticle() != null
                                ? issue.getRepresentativeArticle().getThumbnailUrl() : null)
                .build();
    }
}
