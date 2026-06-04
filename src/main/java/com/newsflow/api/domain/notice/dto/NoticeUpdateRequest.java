package com.newsflow.api.domain.notice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeUpdateRequest {

    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    private String title;

    private String content;
    private Boolean isPinned;
    private Boolean isActive;
    private String targetGate;
    private LocalDateTime publishedAt;
    private LocalDateTime expiredAt;
}