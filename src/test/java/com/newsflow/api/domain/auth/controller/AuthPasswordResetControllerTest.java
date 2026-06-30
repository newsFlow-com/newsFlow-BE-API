package com.newsflow.api.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.auth.service.AuthService;
import com.newsflow.api.domain.log.repository.ApiRequestLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class AuthPasswordResetControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthService authService;
    @MockBean JwtUtil jwtUtil;
    @MockBean ApiRequestLogRepository apiRequestLogRepository;

    @Test
    @DisplayName("POST /password/reset/request — 유효한 이메일로 요청 시 200 반환")
    void requestPasswordReset_returnsOk() throws Exception {
        doNothing().when(authService).requestPasswordReset(eq("user@test.com"));

        mockMvc.perform(post("/api/v1/auth/password/reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("비밀번호 재설정 메일이 발송되었습니다."));
    }

    @Test
    @DisplayName("POST /password/reset/request — 이메일 형식이 아니면 400 반환")
    void requestPasswordReset_returns400_whenEmailInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password/reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "not-an-email"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /password/reset/confirm — 유효한 토큰과 비밀번호로 요청 시 200 반환")
    void confirmPasswordReset_returnsOk() throws Exception {
        doNothing().when(authService).confirmPasswordReset(any());

        mockMvc.perform(post("/api/v1/auth/password/reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("token", "valid-token", "newPassword", "newPass123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("비밀번호가 재설정되었습니다."));
    }

    @Test
    @DisplayName("POST /password/reset/confirm — 토큰 누락 시 400 반환")
    void confirmPasswordReset_returns400_whenTokenMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password/reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("newPassword", "newPass123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /password/reset/confirm — 비밀번호 8자 미만이면 400 반환")
    void confirmPasswordReset_returns400_whenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password/reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("token", "valid-token", "newPassword", "short"))))
                .andExpect(status().isBadRequest());
    }
}
