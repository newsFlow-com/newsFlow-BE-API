package com.newsflow.api.domain.auth.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.auth.dto.*;
import com.newsflow.api.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API (회원가입 / 로그인 / 카카오 OAuth / 토큰 갱신)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("회원가입이 완료되었습니다.", authService.signup(request))
        );
    }

    @Operation(summary = "로그인",
            description = "gate = 'user' (일반) | 'admin' (관리자). 관리자 게이트는 role=admin 계정만 허용.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(authService.login(request))
        );
    }

    @Operation(summary = "카카오 로그인",
            description = "카카오 OAuth 인가 코드로 로그인. 신규 사용자는 자동 가입.")
    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(authService.kakaoLogin(request.getCode()))
        );
    }

    @Operation(summary = "토큰 갱신",
            description = "Refresh Token 으로 새 Access Token 발급.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(authService.refresh(request))
        );
    }

    @Operation(summary = "로그아웃",
            description = "Refresh Token 폐기.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshRequest request,
            @RequestHeader(value = "X-Gate", defaultValue = "user") String gate
    ) {
        authService.logout(request.getRefreshToken(), gate);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 되었습니다.", null));
    }
}