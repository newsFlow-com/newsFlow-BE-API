package com.newsflow.api.domain.trending.dto;

import com.newsflow.api.domain.article.dto.ArticleResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrendingArticleResponse {
    private int rank;
    private ArticleResponse article;
    private long viewCount;
}