package com.newsflow.api.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserCategoryResponse {

    private UUID categoryId;
    private String slug;
    private String name;
    private int preferenceWeight;
}