package com.newsflow.api.domain.notification.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.notification.dto.NotificationResponse;
import com.newsflow.api.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "알림", description = "사용자 알림 조회 및 읽음 처리")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok(notificationService.getMyNotifications(userId, size)));
    }

    @Operation(summary = "미읽음 알림 수 조회", description = "응답 필드: count")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UUID userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "응답 필드: updatedCount")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead(
            @AuthenticationPrincipal UUID userId) {
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("updatedCount", count)));
    }
}
