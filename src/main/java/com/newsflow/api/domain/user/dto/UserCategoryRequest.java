package com.newsflow.api.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryRequest {

    @NotBlank(message = "카테고리 슬러그는 필수입니다.")
    private String categorySlug;

    @Builder.Default
    private int preferenceWeight = 5;
}