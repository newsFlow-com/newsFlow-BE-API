package com.newsflow.api.domain.subscription.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.subscription.dto.SubscriptionRequest;
import com.newsflow.api.domain.subscription.dto.SubscriptionResponse;
import com.newsflow.api.domain.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "구독", description = "키워드·카테고리 구독 관리")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "내 구독 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getMySubscriptions(
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(
                ApiResponse.ok(subscriptionService.getMySubscriptions(userId)));
    }

    @Operation(summary = "구독 추가", description = "subscriptionType: keyword | category")
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(subscriptionService.subscribe(userId, request)));
    }

    @Operation(summary = "구독 취소")
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID subscriptionId) {
        subscriptionService.unsubscribe(userId, subscriptionId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
