package com.newsflow.api.domain.stats.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CalendarMonthResponse { // 💡 public class 명시

    private int year;
    private int month;
    private List<CalendarDaySummary> days;
}