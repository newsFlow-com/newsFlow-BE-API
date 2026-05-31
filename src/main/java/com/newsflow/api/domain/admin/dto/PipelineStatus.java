package com.newsflow.api.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PipelineStatus {
    private String dagId;
    private String status;
    private int collectedCount;
    private int errorCount;
    private String startedAt;
}