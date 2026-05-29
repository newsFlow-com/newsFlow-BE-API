package com.newsflow.api.domain.trending.dto;

import com.newsflow.api.entity.TrendingKeyword;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrendingKeywordResponse {
    private int rank;
    private String word;
    private Double searchVolumeIndex;

    public static TrendingKeywordResponse from(TrendingKeyword t) {
        return TrendingKeywordResponse.builder()
                .rank(t.getRank())
                .word(t.getKeyword().getWord())
                .searchVolumeIndex(t.getSearchVolumeIndex())
                .build();
    }
}