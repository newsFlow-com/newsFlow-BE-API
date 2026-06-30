package com.newsflow.api.domain.banner.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.banner.dto.BannerCreateRequest;
import com.newsflow.api.domain.banner.dto.BannerResponse;
import com.newsflow.api.domain.banner.dto.BannerUpdateRequest;
import com.newsflow.api.domain.banner.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Admin-Banner", description = "관리자 배너 관리 API")
@RestController
@RequestMapping("/api/admin/v1/banners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {

    private final BannerService bannerService;

    @Operation(summary = "배너 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @Valid @RequestBody BannerCreateRequest request,
            @AuthenticationPrincipal UUID adminId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("배너가 생성되었습니다.", bannerService.createBanner(request, adminId))
        );
    }

    @Operation(summary = "배너 수정")
    @PatchMapping("/{bannerId}")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable UUID bannerId,
            @Valid @RequestBody BannerUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("배너가 수정되었습니다.", bannerService.updateBanner(bannerId, request))
        );
    }

    @Operation(summary = "배너 삭제 (비활성화)")
    @DeleteMapping("/{bannerId}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(
            @PathVariable UUID bannerId
    ) {
        bannerService.deleteBanner(bannerId);
        return ResponseEntity.ok(ApiResponse.ok("배너가 삭제되었습니다.", null));
    }
}
