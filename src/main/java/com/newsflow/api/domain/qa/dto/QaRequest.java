package com.newsflow.api.domain.qa.dto;

import jakarta.validation.constraints.NotBlank;

public record QaRequest(
        @NotBlank(message = "질문을 입력해주세요.")
        String question
) {}
