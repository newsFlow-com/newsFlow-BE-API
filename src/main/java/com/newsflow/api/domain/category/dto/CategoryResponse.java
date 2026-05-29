package com.newsflow.api.domain.category.dto;

import com.newsflow.api.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String iconUrl;
    private int displayOrder;
    private List<CategoryResponse> children;

    public static CategoryResponse from(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .iconUrl(c.getIconUrl())
                .displayOrder(c.getDisplayOrder())
                .children(c.getChildren().stream()
                        .filter(Category::isActive) // 객체지향 고도화 시 c.getActiveChildren() 형태로 변경 고려
                        .map(CategoryResponse::from)
                        .toList())
                .build();
    }
}