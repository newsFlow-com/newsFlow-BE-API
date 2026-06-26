package com.newsflow.api.domain.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;

@Getter
public class ContentQualityReviewRequest {

    @NotNull
    private Boolean isCorrect;

    private Map<String, Object> correction;
}
