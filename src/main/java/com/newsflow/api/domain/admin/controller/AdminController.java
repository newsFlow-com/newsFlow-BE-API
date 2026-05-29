package com.newsflow.api.domain.admin.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Article;
import com.newsflow.api.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin", description = "관리자 전용 API (gate=admin 토큰 필요)")
@RestController
@RequestMapping("/api/admin/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // ── 기사 관리 ─────────────────────────────────────────────────

    @Operation(summary = "기사 숨김 처리")
    @PatchMapping("/articles/{articleId}/hide")
    public ResponseEntity<ApiResponse<Void>> hideArticle(@PathVariable UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        article.hide();
        articleRepository.save(article);
        return ResponseEntity.ok(ApiResponse.ok("기사가 숨겨졌습니다.", null));
    }

    @Operation(summary = "기사 복구")
    @PatchMapping("/articles/{articleId}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreArticle(@PathVariable UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        article.restore();
        articleRepository.save(article);
        return ResponseEntity.ok(ApiResponse.ok("기사가 복구되었습니다.", null));
    }

    @Operation(summary = "기사 삭제")
    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        article.delete();
        articleRepository.save(article);
        return ResponseEntity.ok(ApiResponse.ok("기사가 삭제되었습니다.", null));
    }

    // ── 사용자 관리 ───────────────────────────────────────────────

    @Operation(summary = "사용자 목록 조회")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<AdminUserResponse> users = userRepository
                .findAll(PageRequest.of(page, size))
                .stream()
                .map(AdminUserResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @Operation(summary = "사용자 정지")
    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.suspend();
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("사용자가 정지되었습니다.", null));
    }

    @Operation(summary = "사용자 정지 해제")
    @PatchMapping("/users/{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.activate();
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("사용자 정지가 해제되었습니다.", null));
    }

    // ── 대시보드 지표 ─────────────────────────────────────────────

    @Operation(summary = "대시보드 요약 지표",
            description = "전체 기사 수, 활성 사용자 수, 오늘 수집량 등 요약 지표 반환.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardSummary>> getDashboard() {
        long totalArticles = articleRepository.count();
        long totalUsers = userRepository.count();

        return ResponseEntity.ok(ApiResponse.ok(
                DashboardSummary.builder()
                        .totalArticles(totalArticles)
                        .totalUsers(totalUsers)
                        .build()
        ));
    }

    // ── Admin DTOs ────────────────────────────────────────────────

    @Getter
    @Builder
    static class AdminUserResponse {
        private UUID id;
        private String email;
        private String nickname;
        private String role;
        private String status;

        static AdminUserResponse from(User user) {
            return AdminUserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    static class DashboardSummary {
        private long totalArticles;
        private long totalUsers;
    }
}