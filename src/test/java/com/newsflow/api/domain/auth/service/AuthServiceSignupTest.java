package com.newsflow.api.domain.auth.service;

import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.auth.dto.SignupRequest;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceSignupTest {

    @InjectMocks AuthService authService;
    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock EmailService emailService;

    private SignupRequest makeRequest() {
        SignupRequest req = new SignupRequest();
        ReflectionTestUtils.setField(req, "email", "user@test.com");
        ReflectionTestUtils.setField(req, "password", "password123");
        ReflectionTestUtils.setField(req, "nickname", "테스터");
        return req;
    }

    private void stubCommon() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", UUID.randomUUID());
            return u;
        });
        when(jwtUtil.generateAccessToken(any(), any(), any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("refresh-token");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("회원가입 후 이메일 인증 메일이 자동 발송된다")
    void signup_sendsVerificationEmail_afterUserSaved() {
        stubCommon();

        authService.signup(makeRequest());

        verify(emailService).sendVerificationEmail(any(UUID.class), eq("user@test.com"));
    }

    @Test
    @DisplayName("이메일 발송 실패해도 회원가입은 성공한다")
    void signup_succeedsEvenIfEmailServiceThrows() {
        stubCommon();
        doThrow(new RuntimeException("SMTP 오류")).when(emailService)
                .sendVerificationEmail(any(), any());

        assertThatCode(() -> authService.signup(makeRequest()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이메일 발송은 userRepository.save() 이후에 호출된다")
    void signup_emailSentAfterUserPersisted() {
        stubCommon();
        var order = inOrder(userRepository, emailService);

        authService.signup(makeRequest());

        order.verify(userRepository).save(any());
        order.verify(emailService).sendVerificationEmail(any(), any());
    }
}
