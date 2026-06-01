package com.newsflow.api.domain.share.service;

import com.newsflow.api.domain.share.dto.ShareCountResponse;
import com.newsflow.api.domain.share.dto.ShareRequest;
import com.newsflow.api.domain.share.dto.ShareResponse;
import com.newsflow.api.domain.share.repository.ShareLogRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.ShareLog;
import com.newsflow.api.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {

    private final ShareLogRepository shareLogRepository;
    private final UserRepository userRepository;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    // ── 공유 기록 저장 ────────────────────────────────────────────

    @Transactional
    public ShareResponse share(ShareRequest request, UUID userId,
                               HttpServletRequest httpRequest) {
        User user = userId != null
                ? userRepository.findById(userId).orElse(null)
                : null;

        String ipAddress = extractIp(httpRequest);

        ShareLog shareLog = ShareLog.builder()
                .user(user)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .channel(request.getChannel())
                .ipAddress(ipAddress)
                .build();

        shareLogRepository.save(shareLog);
        return ShareResponse.from(shareLog, baseUrl);
    }

    // ── 공유 수 조회 ──────────────────────────────────────────────

    public ShareCountResponse getShareCount(String targetType, UUID targetId) {
        long count = shareLogRepository
                .countByTargetTypeAndTargetId(targetType, targetId);
        return ShareCountResponse.builder()
                .targetId(targetId)
                .targetType(targetType)
                .totalCount(count)
                .build();
    }

    // ── 내부 유틸 ─────────────────────────────────────────────────

    private String extractIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}