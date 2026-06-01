package com.newsflow.api.domain.share.dto;

import com.newsflow.api.entity.ShareLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ShareResponse {

    private UUID id;
    private String targetType;
    private UUID targetId;
    private String channel;
    private LocalDateTime createdAt;

    /**
     * 채널별 공유 URL 생성.
     * FE 에서 바로 사용 가능한 공유 링크 제공.
     */
    private String shareUrl;

    public static ShareResponse from(ShareLog log, String baseUrl) {
        String shareUrl = buildShareUrl(log.getChannel(), log.getTargetType(),
                log.getTargetId(), baseUrl);
        return ShareResponse.builder()
                .id(log.getId())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .channel(log.getChannel())
                .createdAt(log.getCreatedAt())
                .shareUrl(shareUrl)
                .build();
    }

    private static String buildShareUrl(String channel, String targetType,
                                        UUID targetId, String baseUrl) {
        String targetUrl = baseUrl + "/" + targetType + "s/" + targetId;

        return switch (channel) {
            case "kakao"     -> "https://sharer.kakao.com/talk/friends/picker/link?url="
                    + targetUrl;
            case "twitter"   -> "https://twitter.com/intent/tweet?url=" + targetUrl;
            case "link",
                 "clipboard" -> targetUrl;
            default          -> targetUrl;
        };
    }
}