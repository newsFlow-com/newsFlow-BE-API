package com.newsflow.api.domain.admin.dto;

import com.newsflow.api.entity.ContentQualityLog;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class ContentQualityLogResponse {

    private UUID id;
    private UUID articleId;
    private String articleTitle;
    private String checkType;
    private Boolean isCorrect;
    private Map<String, Object> originalValue;
    private Map<String, Object> correction;
    private UUID checkedBy;
    private String createdAt;

    public static ContentQualityLogResponse from(ContentQualityLog log) {
        return ContentQualityLogResponse.builder()
                .id(log.getId())
                .articleId(log.getArticle().getId())
                .articleTitle(log.getArticle().getTitle())
                .checkType(log.getCheckType())
                .isCorrect(log.getIsCorrect())
                .originalValue(log.getOriginalValue())
                .correction(log.getCorrection())
                .checkedBy(log.getCheckedBy())
                .createdAt(log.getCreatedAt() != null
                        ? log.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .build();
    }
}
