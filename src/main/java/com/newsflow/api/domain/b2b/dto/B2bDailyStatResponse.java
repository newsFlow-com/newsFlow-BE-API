package com.newsflow.api.domain.b2b.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class B2bDailyStatResponse {

    private String from;
    private String to;
    private List<DayEntry> data;

    @Getter
    @Builder
    public static class DayEntry {
        private String date;
        private List<CategoryCount> categories;
    }

    @Getter
    @Builder
    public static class CategoryCount {
        private String slug;
        private String name;
        private long count;
    }
}
