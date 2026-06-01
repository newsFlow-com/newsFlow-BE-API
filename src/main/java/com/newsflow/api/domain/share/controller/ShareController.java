package com.newsflow.api.domain.share.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.share.dto.ShareCountResponse;
import com.newsflow.api.domain.share.dto.ShareRequest;
import com.newsflow.api.domain.share.dto.ShareResponse;
import com.newsflow.api.domain.share.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Share", description = "공유 API — 기사/차트/주식 공유 로그 기록 및 공유 URL 생성")
@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @Operation(summary = "공유하기",
            description = "기사/차트/주식 공유 시 로그를 기록하고 채널별 공유 URL을 반환합니다. "
                    + "비로그인 사용자도 사용 가능합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ShareResponse>> share(
            @Valid @RequestBody ShareRequest request,
            @AuthenticationPrincipal UUID userId,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(shareService.share(request, userId, httpRequest))
        );
    }

    @Operation(summary = "공유 수 조회",
            description = "특정 타겟(기사/차트/주식)의 총 공유 수를 반환합니다.")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<ShareCountResponse>> getShareCount(
            @RequestParam String targetType,
            @RequestParam UUID targetId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(shareService.getShareCount(targetType, targetId))
        );
    }
}