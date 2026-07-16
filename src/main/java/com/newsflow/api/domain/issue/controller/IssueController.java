package com.newsflow.api.domain.issue.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.issue.dto.IssueDetailResponse;
import com.newsflow.api.domain.issue.dto.IssueResponse;
import com.newsflow.api.domain.issue.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Issue", description = "이슈 클러스터 조회 API")
@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @Operation(summary = "이슈 목록 조회",
            description = "여러 매체가 함께 다룬 이슈 클러스터 목록. 최신순/기사수순 정렬 지원.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getIssues(
            @Parameter(description = "카테고리 슬러그 (예: economy, politics)")
            @RequestParam(required = false) String category,

            @Parameter(description = "정렬 기준 (latest | count, 기본 latest)")
            @RequestParam(defaultValue = "latest") String sort,

            @Parameter(description = "조회 개수 (기본 20, 최대 50)")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(issueService.getIssues(category, sort, size))
        );
    }

    @Operation(summary = "이슈 상세 조회",
            description = "이슈에 속한 매체별 기사 목록 포함")
    @GetMapping("/{issueId}")
    public ResponseEntity<ApiResponse<IssueDetailResponse>> getIssue(
            @PathVariable UUID issueId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(issueService.getIssue(issueId))
        );
    }
}
