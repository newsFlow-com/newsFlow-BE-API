package com.newsflow.api.domain.source.dto;

import com.newsflow.api.entity.Source;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SourceResponse {

    private UUID id;
    private String name;
    private String domain;
    private String tier;

    public static SourceResponse from(Source source) {
        return SourceResponse.builder()
                .id(source.getId())
                .name(source.getName())
                .domain(source.getDomain())
                .tier(source.getTier())
                .build();
    }
}
