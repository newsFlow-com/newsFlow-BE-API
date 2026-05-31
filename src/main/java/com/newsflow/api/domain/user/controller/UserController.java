package com.newsflow.api.domain.user.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.user.dto.UpdateProfileRequest;
import com.newsflow.api.domain.user.dto.UserCategoryRequest;
import com.newsflow.api.domain.user.dto.UserCategoryResponse;
import com.newsflow.api.domain.user.dto.UserProfileResponse;
import com.newsflow.api.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "사용자 마이페이지 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(userService.getMyProfile(userId))
        );
    }

    @Operation(summary = "프로필 수정",
            description = "닉네임, 프로필 이미지 URL 수정.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(userService.updateProfile(userId, request))
        );
    }

    @Operation(summary = "관심 카테고리 목록 조회")
    @GetMapping("/me/categories")
    public ResponseEntity<ApiResponse<List<UserCategoryResponse>>> getMyCategories(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(userService.getMyCategories(userId))
        );
    }

    @Operation(summary = "관심 카테고리 등록",
            description = "이미 등록된 카테고리면 가중치만 업데이트.")
    @PostMapping("/me/categories")
    public ResponseEntity<ApiResponse<UserCategoryResponse>> addCategory(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UserCategoryRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(userService.addCategory(userId, request))
        );
    }

    @Operation(summary = "관심 카테고리 삭제")
    @DeleteMapping("/me/categories/{categorySlug}")
    public ResponseEntity<ApiResponse<Void>> removeCategory(
            @AuthenticationPrincipal UUID userId,
            @PathVariable String categorySlug
    ) {
        userService.removeCategory(userId, categorySlug);
        return ResponseEntity.ok(ApiResponse.ok("관심 카테고리가 삭제되었습니다.", null));
    }
}