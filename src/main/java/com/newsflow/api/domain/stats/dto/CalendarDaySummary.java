package com.newsflow.api.domain.stats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarDaySummary {

    private LocalDate date;
    private long articleCount;
    private long totalViews;
    private double maxTrending;

    public static CalendarDaySummary from(Object[] row) {
        return CalendarDaySummary.builder()
                .date((LocalDate) row[0])
                .articleCount(((Number) row[1]).longValue())
                .totalViews(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                .maxTrending(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0)
                .build();
    }
}