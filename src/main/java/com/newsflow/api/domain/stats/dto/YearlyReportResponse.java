package com.newsflow.api.domain.stats.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class YearlyReportResponse { // 💡 public class 명시

    private int year;
    private List<DailyArticleResponse.DailyArticleItem> topArticles;
}