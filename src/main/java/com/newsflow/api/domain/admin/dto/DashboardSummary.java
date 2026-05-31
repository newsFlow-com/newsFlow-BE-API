package com.newsflow.api.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummary {

    private long totalArticles;         // 전체 기사 수
    private long totalUsers;            // 전체 사용자 수
    private long todayCollected;        // 오늘 수집된 기사 수
    private long pendingReports;        // 처리 대기 신고 수
    private PipelineStatus latestPipeline; // 최근 파이프라인 상태
}