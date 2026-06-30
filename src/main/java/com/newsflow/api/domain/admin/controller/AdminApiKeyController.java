package com.newsflow.api.domain.admin.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.admin.dto.ApiKeyCreateRequest;
import com.newsflow.api.domain.admin.dto.ApiKeyCreateResponse;
import com.newsflow.api.domain.admin.dto.ApiKeyResponse;
import com.newsflow.api.domain.admin.repository.ApiKeyRepository;
import com.newsflow.api.entity.ApiKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Tag(name = "Admin - API Key", description = "B2B API 키 관리")
@RestController
@RequestMapping("/api/admin/v1/api-keys")
@RequiredArgsConstructor
public class AdminApiKeyController {

    private final ApiKeyRepository apiKeyRepository;

    @Operation(summary = "API 키 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> listKeys() {
        List<ApiKeyResponse> keys = apiKeyRepository.findAll().stream()
                .map(ApiKeyResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(keys));
    }

    @Operation(summary = "API 키 발급", description = "rawKey는 발급 시 1회만 반환됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> createKey(
            @Valid @RequestBody ApiKeyCreateRequest request) {

        String rawKey = generateRawKey();
        String keyHash = sha256Hex(rawKey);

        ApiKey apiKey = ApiKey.builder()
                .keyHash(keyHash)
                .clientName(request.getClientName())
                .rateLimitPerHour(request.getRateLimitPerHour())
                .expiresAt(request.getExpiresAt())
                .build();
        apiKeyRepository.save(apiKey);

        return ResponseEntity.ok(ApiResponse.ok(ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .clientName(apiKey.getClientName())
                .rawKey(rawKey)
                .rateLimitPerHour(apiKey.getRateLimitPerHour())
                .expiresAt(apiKey.getExpiresAt())
                .createdAt(apiKey.getCreatedAt())
                .build()));
    }

    @Operation(summary = "API 키 폐기")
    @DeleteMapping("/{keyId}")
    public ResponseEntity<ApiResponse<Void>> revokeKey(@PathVariable UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.API_KEY_NOT_FOUND));
        apiKey.revoke();
        apiKeyRepository.save(apiKey);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private static String generateRawKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
