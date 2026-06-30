package com.newsflow.api.domain.banner.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannerUpdateRequest {

    private String title;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
