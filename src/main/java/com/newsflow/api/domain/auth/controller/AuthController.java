package com.newsflow.api.domain.auth.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.auth.dto.*;
import com.newsflow.api.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Auth", description = "인증 API (회원가입 / 로그인 / 카카오 OAuth / 토큰 갱신)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 중복 확인", description = "available: true → 사용 가능, false → 이미 사용 중")
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("available", authService.isEmailAvailable(email))));
    }

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

    @Operation(summary = "비밀번호 변경",
            description = "현재 비밀번호 확인 후 변경. 변경 시 모든 Refresh Token 폐기.")
    @PostMapping("/password/change")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호가 변경되었습니다.", null));
    }

    @Operation(summary = "이메일 인증 메일 발송", description = "로그인된 사용자에게 인증 메일 발송.")
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(
            @AuthenticationPrincipal UUID userId
    ) {
        authService.sendVerificationEmail(userId);
        return ResponseEntity.ok(ApiResponse.ok("인증 메일이 발송되었습니다.", null));
    }

    @Operation(summary = "이메일 인증 확인", description = "메일의 링크로 접근. 인증 토큰 검증 후 계정 인증 완료.")
    @GetMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @RequestParam String token
    ) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.ok("이메일 인증이 완료되었습니다.", null));
    }

    @Operation(summary = "비밀번호 재설정 메일 발송",
            description = "입력한 이메일로 재설정 링크를 발송합니다. 이메일 존재 여부와 무관하게 성공 응답을 반환합니다.")
    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("비밀번호 재설정 메일이 발송되었습니다.", null));
    }

    @Operation(summary = "비밀번호 재설정 확인",
            description = "재설정 토큰 검증 후 비밀번호를 변경합니다. 토큰 유효시간은 30분입니다.")
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest request
    ) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호가 재설정되었습니다.", null));
    }
}