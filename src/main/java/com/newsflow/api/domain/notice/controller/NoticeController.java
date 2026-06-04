package com.newsflow.api.domain.notice.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.notice.dto.NoticeResponse;
import com.newsflow.api.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notice", description = "공지사항 API")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 목록 조회",
            description = "고정 공지 우선, 노출 기간 유효한 공지사항을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getNotices(
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(noticeService.getNoticesForUser(size))
        );
    }

    @Operation(summary = "고정 공지사항 조회",
            description = "메인 화면 상단에 노출할 고정 공지사항 목록을 반환합니다.")
    @GetMapping("/pinned")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getPinnedNotices() {
        return ResponseEntity.ok(
                ApiResponse.ok(noticeService.getPinnedNotices())
        );
    }

    @Operation(summary = "공지사항 상세 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNotice(
            @PathVariable UUID noticeId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(noticeService.getNotice(noticeId))
        );
    }
}