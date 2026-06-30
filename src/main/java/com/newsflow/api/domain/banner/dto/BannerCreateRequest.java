package com.newsflow.api.domain.banner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannerCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String imageUrl;

    private String linkUrl;

    /** main_top | sidebar | article_bottom | popup */
    @NotBlank
    private String position;

    private int displayOrder;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
