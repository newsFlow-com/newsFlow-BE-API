package com.newsflow.api.domain.share.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ShareCountResponse {

    private UUID targetId;
    private String targetType;
    private long totalCount;
}