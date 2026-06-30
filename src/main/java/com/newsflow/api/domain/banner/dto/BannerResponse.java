package com.newsflow.api.domain.banner.dto;

import com.newsflow.api.entity.Banner;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BannerResponse {

    private UUID id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private int displayOrder;
    private boolean isActive;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int clickCount;
    private int impressionCount;

    public static BannerResponse from(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .position(banner.getPosition())
                .displayOrder(banner.getDisplayOrder())
                .isActive(banner.isActive())
                .startAt(banner.getStartAt())
                .endAt(banner.getEndAt())
                .clickCount(banner.getClickCount())
                .impressionCount(banner.getImpressionCount())
                .build();
    }
}
