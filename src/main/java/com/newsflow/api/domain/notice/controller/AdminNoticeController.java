package com.newsflow.api.domain.notice.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.notice.dto.NoticeCreateRequest;
import com.newsflow.api.domain.notice.dto.NoticeResponse;
import com.newsflow.api.domain.notice.dto.NoticeUpdateRequest;
import com.newsflow.api.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin-Notice", description = "관리자 공지사항 관리 API")
@RestController
@RequestMapping("/api/admin/v1/notices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "관리자용 공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getNoticesForAdmin(
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(noticeService.getNoticesForAdmin(size))
        );
    }

    @Operation(summary = "공지사항 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            @Valid @RequestBody NoticeCreateRequest request,
            @AuthenticationPrincipal UUID adminId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("공지사항이 생성되었습니다.",
                        noticeService.createNotice(request, adminId))
        );
    }

    @Operation(summary = "공지사항 수정")
    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(
            @PathVariable UUID noticeId,
            @Valid @RequestBody NoticeUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("공지사항이 수정되었습니다.",
                        noticeService.updateNotice(noticeId, request))
        );
    }

    @Operation(summary = "공지사항 삭제 (비활성화)")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable UUID noticeId
    ) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.ok("공지사항이 삭제되었습니다.", null));
    }
}