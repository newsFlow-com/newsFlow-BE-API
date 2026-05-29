package com.newsflow.api.domain.article.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.common.dto.CursorPageResponse;
import com.newsflow.api.domain.article.dto.ArticleDetailResponse;
import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.domain.article.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Article", description = "기사 조회 API")
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "기사 목록 조회",
            description = "카테고리 필터 + 키워드 검색 + 커서 페이지네이션. cursor 없으면 첫 페이지.")
    @GetMapping
    public ResponseEntity<ApiResponse<CursorPageResponse<ArticleResponse>>> getArticles(
            @Parameter(description = "카테고리 슬러그 (예: economy, politics)")
            @RequestParam(required = false) String category,

            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "커서 (이전 응답의 nextCursor 값)")
            @RequestParam(required = false) String cursor,

            @Parameter(description = "페이지 크기 (기본 20, 최대 50)")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(articleService.getArticles(category, keyword, cursor, size))
        );
    }

    @Operation(summary = "기사 상세 조회",
            description = "기사 ID로 상세 정보 조회. 조회 시 Redis 조회수 증가.")
    @GetMapping("/{articleId}")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> getArticle(
            @PathVariable UUID articleId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(articleService.getArticle(articleId))
        );
    }
}