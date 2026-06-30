package com.newsflow.api.domain.banner.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.banner.dto.BannerResponse;
import com.newsflow.api.domain.banner.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Banner", description = "배너 API")
@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "활성 배너 목록 조회",
            description = "노출 기간이 유효한 배너 목록을 반환합니다. "
                    + "position 파라미터로 노출 위치를 필터링할 수 있습니다 "
                    + "(main_top | sidebar | article_bottom | popup).")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners(
            @Parameter(description = "노출 위치 필터 (생략 시 전체)")
            @RequestParam(required = false) String position
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(bannerService.getActiveBanners(position))
        );
    }
}
