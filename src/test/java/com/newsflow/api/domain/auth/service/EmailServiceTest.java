package com.newsflow.api.domain.auth.service;

import com.newsflow.api.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks EmailService emailService;
    @Mock JavaMailSender mailSender;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "baseUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(emailService, "senderEmail", "noreply@newsflow.com");
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 발송 시 Redis에 TTL 30분으로 토큰을 저장한다")
    void sendPasswordResetEmail_savesTokenWithTtl30Minutes() {
        UUID userId = UUID.randomUUID();

        emailService.sendPasswordResetEmail(userId, "user@test.com");

        verify(valueOps).set(
                startsWith("password:reset:"),
                eq(userId.toString()),
                eq(30L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 발송 시 메일을 1회 전송한다")
    void sendPasswordResetEmail_sendsOneMail() {
        UUID userId = UUID.randomUUID();

        emailService.sendPasswordResetEmail(userId, "user@test.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly("user@test.com");
        assertThat(sent.getSubject()).contains("비밀번호 재설정");
    }

    @Test
    @DisplayName("유효한 재설정 토큰으로 userId를 반환한다")
    void verifyResetToken_returnsUserId_whenTokenValid() {
        UUID userId = UUID.randomUUID();
        String token = "valid-reset-token";
        when(valueOps.get("password:reset:" + token)).thenReturn(userId.toString());

        UUID result = emailService.verifyResetToken(token);

        assertThat(result).isEqualTo(userId);
    }

    @Test
    @DisplayName("유효한 재설정 토큰 검증 후 Redis 키를 삭제한다")
    void verifyResetToken_deletesRedisKey_afterVerification() {
        UUID userId = UUID.randomUUID();
        String token = "valid-reset-token";
        when(valueOps.get("password:reset:" + token)).thenReturn(userId.toString());

        emailService.verifyResetToken(token);

        verify(redisTemplate).delete("password:reset:" + token);
    }

    @Test
    @DisplayName("만료되거나 존재하지 않는 토큰으로 BusinessException 발생")
    void verifyResetToken_throwsBusinessException_whenTokenNotFound() {
        String token = "expired-token";
        when(valueOps.get("password:reset:" + token)).thenReturn(null);

        assertThatThrownBy(() -> emailService.verifyResetToken(token))
                .isInstanceOf(BusinessException.class);
    }
}
