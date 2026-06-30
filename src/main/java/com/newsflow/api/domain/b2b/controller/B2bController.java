package com.newsflow.api.domain.b2b.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.b2b.dto.B2bDailyStatResponse;
import com.newsflow.api.domain.b2b.dto.B2bKeywordStatResponse;
import com.newsflow.api.domain.b2b.dto.B2bSentimentStatResponse;
import com.newsflow.api.domain.b2b.service.B2bService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "B2B Stats", description = "B2B 데이터 통계 API (X-API-Key 인증 필수)")
@RestController
@RequestMapping("/api/b2b/v1/stats")
@RequiredArgsConstructor
public class B2bController {

    private final B2bService b2bService;

    @Operation(summary = "일별 기사 수 통계",
            description = "날짜 범위 내 카테고리별 기사 수 집계.")
    @SecurityRequirement(name = "X-API-Key")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<B2bDailyStatResponse>> getDailyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(ApiResponse.ok(b2bService.getDailyStats(from, to, category)));
    }

    @Operation(summary = "감성 분포 통계",
            description = "날짜 범위 내 일별 positive/negative/neutral 기사 수 집계.")
    @SecurityRequirement(name = "X-API-Key")
    @GetMapping("/sentiment")
    public ResponseEntity<ApiResponse<B2bSentimentStatResponse>> getSentimentStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(ApiResponse.ok(b2bService.getSentimentStats(from, to, category)));
    }

    @Operation(summary = "트렌딩 키워드 통계",
            description = "날짜 범위 내 상위 트렌딩 키워드 집계.")
    @SecurityRequirement(name = "X-API-Key")
    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<B2bKeywordStatResponse>> getKeywordStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(b2bService.getKeywordStats(from, to, limit)));
    }
}
