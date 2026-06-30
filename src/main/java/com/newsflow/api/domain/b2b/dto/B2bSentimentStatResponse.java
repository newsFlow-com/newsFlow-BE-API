package com.newsflow.api.domain.b2b.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class B2bSentimentStatResponse {

    private String from;
    private String to;
    private String category;
    private List<DaySentiment> data;

    @Getter
    @Builder
    public static class DaySentiment {
        private String date;
        private long positive;
        private long negative;
        private long neutral;
        private long total;
    }
}
