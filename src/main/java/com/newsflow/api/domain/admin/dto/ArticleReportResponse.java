package com.newsflow.api.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ArticleReportResponse {

    private UUID id;
    private UUID articleId;
    private String articleTitle;
    private String reason;
    private String detail;
    private String status;
    private String reporterEmail;
    private String createdAt;
}