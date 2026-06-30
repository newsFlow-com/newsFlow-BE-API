package com.newsflow.api.domain.stats.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class WeeklyReportResponse {

    private int year;
    private int week;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyArticleResponse.DailyArticleItem> topArticles;
}
