package com.newsflow.api.domain.source.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SourceSentimentStatResponse {

    private UUID sourceId;
    private String sourceName;
    private String from;
    private String to;
    private long positiveCount;
    private long negativeCount;
    private long neutralCount;
    private long totalCount;
    private List<CategoryBreakdown> byCategory;

    @Getter
    @Builder
    public static class CategoryBreakdown {
        private String categorySlug;
        private String categoryName;
        private long positiveCount;
        private long negativeCount;
        private long neutralCount;
    }
}
