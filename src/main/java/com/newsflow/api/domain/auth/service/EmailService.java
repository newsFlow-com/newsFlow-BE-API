package com.newsflow.api.domain.auth.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String TOKEN_PREFIX = "email:verify:";
    private static final long TOKEN_TTL_HOURS = 24;

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
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

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("[NewsFlow] 이메일 인증");
        message.setText("아래 링크를 클릭해 이메일 인증을 완료하세요.\n\n" + verifyUrl
                + "\n\n링크는 24시간 후 만료됩니다.");
        mailSender.send(message);
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
}
