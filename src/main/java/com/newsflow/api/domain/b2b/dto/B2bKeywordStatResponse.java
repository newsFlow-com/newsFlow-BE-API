package com.newsflow.api.domain.b2b.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class B2bKeywordStatResponse {

    private String from;
    private String to;
    private List<KeywordEntry> keywords;

    @Getter
    @Builder
    public static class KeywordEntry {
        private String keyword;
        private long mentionCount;
        private double avgRank;
        private int bestRank;
    }
}
