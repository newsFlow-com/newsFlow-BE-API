package com.newsflow.api.domain.notice.dto;

import com.newsflow.api.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NoticeResponse {

    private UUID id;
    private String title;
    private String content;
    private boolean isPinned;
    private String targetGate;
    private LocalDateTime publishedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private String authorNickname;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .isPinned(notice.isPinned())
                .targetGate(notice.getTargetGate())
                .publishedAt(notice.getPublishedAt())
                .expiredAt(notice.getExpiredAt())
                .createdAt(notice.getCreatedAt())
                .authorNickname(notice.getAuthor() != null
                        ? notice.getAuthor().getNickname() : null)
                .build();
    }
}