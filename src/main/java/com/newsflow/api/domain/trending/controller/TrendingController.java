package com.newsflow.api.domain.trending.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.trending.dto.TrendingArticleResponse;
import com.newsflow.api.domain.trending.dto.TrendingKeywordResponse;
import com.newsflow.api.domain.trending.service.TrendingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Trending", description = "실시간 트렌딩 API")
@RestController
@RequestMapping("/api/v1/trending")
@RequiredArgsConstructor
public class TrendingController {

    private final TrendingService trendingService;

    @Operation(summary = "실시간 인기 기사",
            description = "Redis Sorted Set 기반 실시간 인기 기사 목록.")
    @GetMapping("/articles")
    public ResponseEntity<ApiResponse<List<TrendingArticleResponse>>> getTrendingArticles(
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(trendingService.getTrendingArticles(size))
        );
    }

    @Operation(summary = "급상승 키워드",
            description = "PyTrends 기반 오늘의 급상승 검색 키워드 목록.")
    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<TrendingKeywordResponse>>> getTrendingKeywords(
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(trendingService.getTrendingKeywords(size))
        );
    }
}