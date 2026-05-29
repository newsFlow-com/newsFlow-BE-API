package com.newsflow.api.domain.stats.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MonthlyReportResponse { // 💡 public class 명시

    private int year;
    private int month;
    private List<DailyArticleResponse.DailyArticleItem> topArticles;
}