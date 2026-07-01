package com.newsflow.api.domain.auth.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String TOKEN_PREFIX = "email:verify:";
    private static final long TOKEN_TTL_HOURS = 24;

    private static final String RESET_TOKEN_PREFIX = "password:reset:";
    private static final long RESET_TOKEN_TTL_MINUTES = 30;

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.from}")
    private String senderEmail;

    public void sendVerificationEmail(UUID userId, String toEmail) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token,
                userId.toString(),
                TOKEN_TTL_HOURS,
                TimeUnit.HOURS
        );

        String verifyUrl = baseUrl + "/api/v1/auth/email/verify?token=" + token;

        send(toEmail, "[NewsFlow] 이메일 인증",
                "아래 링크를 클릭해 이메일 인증을 완료하세요.\n\n" + verifyUrl
                + "\n\n링크는 24시간 후 만료됩니다.");
    }

    public UUID verifyToken(String token) {
        String key = TOKEN_PREFIX + token;
        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        redisTemplate.delete(key);
        return UUID.fromString(userIdStr);
    }

    public void sendPasswordResetEmail(UUID userId, String toEmail) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                RESET_TOKEN_PREFIX + token,
                userId.toString(),
                RESET_TOKEN_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        String resetUrl = baseUrl + "/reset-password?token=" + token;
        send(toEmail, "[NewsFlow] 비밀번호 재설정",
                "아래 링크를 클릭해 비밀번호를 재설정하세요.\n\n" + resetUrl
                + "\n\n링크는 30분 후 만료됩니다.");
    }

    public void sendSubscriptionNotification(String toEmail, String articleTitle,
                                             String articleId, String subscriptionValue) {
        String articleUrl = baseUrl + "/articles/" + articleId;
        send(toEmail, "[NewsFlow] 새 기사 알림 — " + articleTitle,
                "'" + subscriptionValue + "' 구독 기사가 등록되었습니다.\n\n"
                + "제목: " + articleTitle + "\n"
                + "링크: " + articleUrl);
    }

    public UUID verifyResetToken(String token) {
        String key = RESET_TOKEN_PREFIX + token;
        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        redisTemplate.delete(key);
        return UUID.fromString(userIdStr);
    }

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.debug("메일 발송 완료: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("메일 발송 실패: to={}, cause={}", to, e.getMessage(), e);
            throw e;
        }
    }
}
