package com.newsflow.api.domain.stats.dto;

import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.entity.DailyArticleStat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyArticleResponse { // 💡 public class 명시

    private LocalDate date;
    private int totalCount;
    private List<DailyArticleItem> articles;

    @Getter
    @Builder
    public static class DailyArticleItem {
        private ArticleResponse article;
        private int viewCount;
        private int bookmarkCount;
        private int shareCount;
        private double trendingScore;

        public static DailyArticleItem from(DailyArticleStat stat) {
            return DailyArticleItem.builder()
                    .article(ArticleResponse.from(stat.getArticle()))
                    .viewCount(stat.getViewCount())
                    .bookmarkCount(stat.getBookmarkCount())
                    .shareCount(stat.getShareCount())
                    .trendingScore(stat.getTrendingScore())
                    .build();
        }
    }
}