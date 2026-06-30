package com.newsflow.api.domain.subscription.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.subscription.dto.SubscriptionRequest;
import com.newsflow.api.domain.subscription.dto.SubscriptionResponse;
import com.newsflow.api.domain.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getMySubscriptions(
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(
                ApiResponse.ok(subscriptionService.getMySubscriptions(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(subscriptionService.subscribe(userId, request)));
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID subscriptionId) {
        subscriptionService.unsubscribe(userId, subscriptionId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
