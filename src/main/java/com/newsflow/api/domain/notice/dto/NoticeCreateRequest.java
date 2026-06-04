package com.newsflow.api.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private boolean isPinned = false;

    /**
     * user | admin | all
     */
    @Pattern(regexp = "user|admin|all", message = "targetGate는 user, admin, all 중 하나여야 합니다.")
    private String targetGate = "user";

    private LocalDateTime publishedAt;
    private LocalDateTime expiredAt;
}