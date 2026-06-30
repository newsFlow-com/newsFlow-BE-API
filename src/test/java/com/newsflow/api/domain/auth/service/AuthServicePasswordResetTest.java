package com.newsflow.api.domain.auth.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.auth.dto.PasswordResetConfirmRequest;
import com.newsflow.api.domain.auth.repository.RefreshTokenRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServicePasswordResetTest {

    @InjectMocks AuthService authService;
    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock EmailService emailService;

    @Test
    @DisplayName("존재하는 활성 사용자의 이메일로 요청 시 재설정 이메일을 발송한다")
    void requestPasswordReset_sendsEmail_whenUserExists() {
        UUID userId = UUID.randomUUID();
        String email = "user@test.com";
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(email);
        when(user.isActive()).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authService.requestPasswordReset(email);

        verify(emailService).sendPasswordResetEmail(userId, email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 요청해도 예외 없이 성공한다 (이메일 존재 여부 미노출)")
    void requestPasswordReset_doesNotThrow_whenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatCode(() -> authService.requestPasswordReset("nobody@test.com"))
                .doesNotThrowAnyException();

        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    @DisplayName("정지된 사용자의 이메일로 요청 시 이메일을 발송하지 않는다")
    void requestPasswordReset_doesNotSendEmail_whenUserSuspended() {
        User user = mock(User.class);
        when(user.isActive()).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        authService.requestPasswordReset("suspended@test.com");

        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    @DisplayName("유효한 토큰으로 비밀번호 재설정 시 changePassword와 토큰 폐기가 호출된다")
    void confirmPasswordReset_changesPasswordAndRevokesTokens() {
        UUID userId = UUID.randomUUID();
        String token = "valid-token";
        String newPassword = "newPass123";
        String encodedPassword = "encoded_newPass123";

        User user = mock(User.class);
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest();
        ReflectionTestUtils.setField(request, "token", token);
        ReflectionTestUtils.setField(request, "newPassword", newPassword);

        when(emailService.verifyResetToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        authService.confirmPasswordReset(request);

        verify(user).changePassword(encodedPassword);
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    @DisplayName("만료된 토큰으로 비밀번호 재설정 시 BusinessException 발생")
    void confirmPasswordReset_throwsWhenTokenInvalid() {
        String token = "expired-token";
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest();
        ReflectionTestUtils.setField(request, "token", token);
        ReflectionTestUtils.setField(request, "newPassword", "newPass123");

        when(emailService.verifyResetToken(token))
                .thenThrow(new BusinessException(
                        com.newsflow.api.common.exception.ErrorCode.INVALID_TOKEN));

        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class,
                () -> authService.confirmPasswordReset(request));
    }
}
