package com.newsflow.api.domain.source.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.source.dto.SourceResponse;
import com.newsflow.api.domain.source.dto.SourceSentimentStatResponse;
import com.newsflow.api.domain.source.service.SourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Source", description = "매체(언론사) 조회 및 논조 비교 API")
@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;

    @Operation(summary = "매체 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SourceResponse>>> getSources() {
        return ResponseEntity.ok(ApiResponse.ok(sourceService.getSources()));
    }

    @Operation(summary = "매체별 감성 통계 조회",
            description = "최근 N일간 카테고리별 긍정/부정/중립 기사 비율 (언론사 논조 비교)")
    @GetMapping("/{sourceId}/sentiment-stats")
    public ResponseEntity<ApiResponse<SourceSentimentStatResponse>> getSentimentStats(
            @PathVariable UUID sourceId,
            @Parameter(description = "집계 기간 (일, 기본 30)")
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(ApiResponse.ok(sourceService.getSentimentStats(sourceId, days)));
    }
}
