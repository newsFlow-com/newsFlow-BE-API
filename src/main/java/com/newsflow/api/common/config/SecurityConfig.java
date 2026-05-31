package com.newsflow.api.common.config;

import com.newsflow.api.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── 공개 엔드포인트 ──────────────────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/articles/**",
                                "/api/v1/categories/**",
                                "/api/v1/trending/**",
                                "/api/v1/stats/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/actuator/**"
                        ).permitAll()

                        // ── 관리자 게이트 ─────────────────────────────────
                        // /api/admin/** 은 ROLE_ADMIN + gate=admin 검증
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ── 나머지는 인증 필요 ────────────────────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── JWT 인증 필터 ──────────────────────────────────────────────
    @Slf4j
    @RequiredArgsConstructor
    static class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            String token = resolveToken(request);

            if (token != null && jwtUtil.validateToken(token)) {
                UUID userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);
                String gate = jwtUtil.extractGate(token);

                // /api/admin/** 요청에 user 게이트 토큰 접근 차단
                String uri = request.getRequestURI();
                if (uri.startsWith("/api/admin/") && !"admin".equals(gate)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "관리자 게이트 토큰이 필요합니다.");
                    return;
                }

                var auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }

        private String resolveToken(HttpServletRequest request) {
            String bearer = request.getHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
            return null;
        }
    }
}