package com.newsflow.api.domain.stats.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.stats.dto.CalendarMonthResponse;
import com.newsflow.api.domain.stats.dto.DailyArticleResponse;
import com.newsflow.api.domain.stats.dto.MonthlyReportResponse;
import com.newsflow.api.domain.stats.dto.YearlyReportResponse;
import com.newsflow.api.domain.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Stats", description = "날짜별 집계 / 달력 뷰 / 리포트 API ⭐")
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "월별 달력 요약",
            description = "달력 뷰 핵심 API. 특정 연월의 날짜별 기사 수 / 조회수 / 트렌딩 점수 반환.")
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<ApiResponse<CalendarMonthResponse>> getCalendar(
            @Parameter(description = "연도 (예: 2025)") @PathVariable int year,
            @Parameter(description = "월 (1~12)") @PathVariable int month
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(statsService.getCalendarMonth(year, month))
        );
    }

    @Operation(summary = "날짜별 기사 목록",
            description = "달력에서 날짜 클릭 시 호출. 해당일 기사를 트렌딩 점수 내림차순으로 반환.")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyArticleResponse>> getDailyArticles(
            @Parameter(description = "날짜 (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(statsService.getDailyArticles(date, size))
        );
    }

    @Operation(summary = "월별 리포트",
            description = "특정 연월의 상위 트렌딩 기사 Top 10 반환.")
    @GetMapping("/report/monthly/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(statsService.getMonthlyReport(year, month))
        );
    }

    @Operation(summary = "연별 리포트",
            description = "특정 연도의 상위 트렌딩 기사 Top 10 반환.")
    @GetMapping("/report/yearly/{year}")
    public ResponseEntity<ApiResponse<YearlyReportResponse>> getYearlyReport(
            @PathVariable int year
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(statsService.getYearlyReport(year))
        );
    }
}