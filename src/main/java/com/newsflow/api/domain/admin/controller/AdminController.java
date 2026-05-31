package com.newsflow.api.domain.admin.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.admin.dto.AdminUserResponse;
import com.newsflow.api.domain.admin.dto.ArticleReportResponse;
import com.newsflow.api.domain.admin.dto.DashboardSummary;
import com.newsflow.api.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin", description = "관리자 전용 API (gate=admin 토큰 필요)")
@RestController
@RequestMapping("/api/admin/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ── 기사 관리 ─────────────────────────────────────────────────

    @Operation(summary = "기사 숨김 처리")
    @PatchMapping("/articles/{articleId}/hide")
    public ResponseEntity<ApiResponse<Void>> hideArticle(@PathVariable UUID articleId) {
        adminService.hideArticle(articleId);
        return ResponseEntity.ok(ApiResponse.ok("기사가 숨겨졌습니다.", null));
    }

    @Operation(summary = "기사 복구")
    @PatchMapping("/articles/{articleId}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreArticle(@PathVariable UUID articleId) {
        adminService.restoreArticle(articleId);
        return ResponseEntity.ok(ApiResponse.ok("기사가 복구되었습니다.", null));
    }

    @Operation(summary = "기사 삭제")
    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable UUID articleId) {
        adminService.deleteArticle(articleId);
        return ResponseEntity.ok(ApiResponse.ok("기사가 삭제되었습니다.", null));
    }

    // ── 사용자 관리 ───────────────────────────────────────────────

    @Operation(summary = "사용자 목록 조회")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getUsers(page, size)));
    }

    @Operation(summary = "사용자 정지")
    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendUser(@PathVariable UUID userId) {
        adminService.suspendUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("사용자가 정지되었습니다.", null));
    }

    @Operation(summary = "사용자 정지 해제")
    @PatchMapping("/users/{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable UUID userId) {
        adminService.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("사용자 정지가 해제되었습니다.", null));
    }

    // ── 신고 관리 ─────────────────────────────────────────────────

    @Operation(summary = "처리 대기 신고 목록")
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<ArticleReportResponse>>> getPendingReports(
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getPendingReports(size)));
    }

    @Operation(summary = "신고 처리 완료")
    @PatchMapping("/reports/{reportId}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolveReport(
            @PathVariable UUID reportId,
            @RequestParam String resolution,
            @AuthenticationPrincipal UUID adminId
    ) {
        adminService.resolveReport(reportId, adminId, resolution);
        return ResponseEntity.ok(ApiResponse.ok("신고가 처리되었습니다.", null));
    }

    @Operation(summary = "신고 기각")
    @PatchMapping("/reports/{reportId}/dismiss")
    public ResponseEntity<ApiResponse<Void>> dismissReport(
            @PathVariable UUID reportId,
            @RequestParam String resolution,
            @AuthenticationPrincipal UUID adminId
    ) {
        adminService.dismissReport(reportId, adminId, resolution);
        return ResponseEntity.ok(ApiResponse.ok("신고가 기각되었습니다.", null));
    }

    // ── 대시보드 ──────────────────────────────────────────────────

    @Operation(summary = "대시보드 요약 지표",
            description = "전체 기사/유저 수, 오늘 수집량, 대기 신고 수, 최근 파이프라인 상태.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardSummary>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboard()));
    }
}