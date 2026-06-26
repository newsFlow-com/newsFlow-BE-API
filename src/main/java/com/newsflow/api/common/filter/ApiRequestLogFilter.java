package com.newsflow.api.common.filter;

import com.newsflow.api.domain.log.repository.ApiRequestLogRepository;
import com.newsflow.api.entity.ApiRequestLog;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApiRequestLogFilter extends OncePerRequestFilter {

    private static final Set<String> SKIP_PREFIXES = Set.of(
            "/swagger-ui", "/v3/api-docs", "/api-docs", "/actuator"
    );

    private final ApiRequestLogRepository logRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (SKIP_PREFIXES.stream().anyMatch(uri::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            int elapsed = (int) (System.currentTimeMillis() - start);

            UUID userId = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UUID id) {
                userId = id;
            }

            String gate = uri.startsWith("/api/admin/") ? "admin" : "user";
            String ip = extractIp(request);

            try {
                logRepository.save(ApiRequestLog.builder()
                        .gate(gate)
                        .method(request.getMethod())
                        .endpoint(uri)
                        .statusCode(wrappedResponse.getStatus())
                        .responseTimeMs(elapsed)
                        .userId(userId)
                        .ipAddress(ip)
                        .userAgent(request.getHeader("User-Agent"))
                        .build());
            } catch (Exception ignored) {
                // 로그 저장 실패가 요청 처리에 영향을 주지 않도록 예외 흡수
            }

            wrappedResponse.copyBodyToResponse();
        }
    }

    private String extractIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
