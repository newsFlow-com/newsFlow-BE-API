package com.newsflow.api.domain.share.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.UUID;

// ── ShareRequest ──────────────────────────────────────────────────
@Getter
public class ShareRequest {

    @NotNull(message = "공유 대상 ID는 필수입니다.")
    private UUID targetId;

    /**
     * article | chart | stock
     */
    @NotBlank(message = "공유 대상 타입은 필수입니다.")
    @Pattern(regexp = "article|chart|stock", message = "targetType은 article, chart, stock 중 하나여야 합니다.")
    private String targetType;

    /**
     * kakao | link | clipboard | twitter
     */
    @NotBlank(message = "공유 채널은 필수입니다.")
    @Pattern(regexp = "kakao|link|clipboard|twitter", message = "channel은 kakao, link, clipboard, twitter 중 하나여야 합니다.")
    private String channel;
}