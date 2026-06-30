package com.newsflow.api.domain.banner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.banner.dto.BannerResponse;
import com.newsflow.api.domain.banner.service.BannerService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AdminBannerController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class AdminBannerControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean BannerService bannerService;
    @MockBean JwtUtil jwtUtil;
    @MockBean ApiRequestLogRepository apiRequestLogRepository;

    @Test
    @DisplayName("POST /api/admin/v1/banners — 유효한 요청으로 배너 생성 성공")
    void createBanner_returnsOk() throws Exception {
        BannerResponse response = BannerResponse.builder()
                .id(UUID.randomUUID())
                .title("신규 배너")
                .imageUrl("http://img.test/1.jpg")
                .position("main_top")
                .displayOrder(1)
                .build();
        when(bannerService.createBanner(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/v1/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "신규 배너",
                                "imageUrl", "http://img.test/1.jpg",
                                "position", "main_top"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("신규 배너"));
    }

    @Test
    @DisplayName("POST /api/admin/v1/banners — title 누락 시 400 반환")
    void createBanner_returns400_whenTitleMissing() throws Exception {
        mockMvc.perform(post("/api/admin/v1/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "imageUrl", "http://img.test/1.jpg",
                                "position", "main_top"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/admin/v1/banners/{id} — 배너 수정 성공")
    void updateBanner_returnsOk() throws Exception {
        UUID bannerId = UUID.randomUUID();
        BannerResponse response = BannerResponse.builder()
                .id(bannerId)
                .title("수정된 배너")
                .imageUrl("http://img.test/1.jpg")
                .position("sidebar")
                .build();
        when(bannerService.updateBanner(eq(bannerId), any())).thenReturn(response);

        mockMvc.perform(patch("/api/admin/v1/banners/" + bannerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "수정된 배너"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("수정된 배너"));
    }

    @Test
    @DisplayName("DELETE /api/admin/v1/banners/{id} — 배너 삭제 성공")
    void deleteBanner_returnsOk() throws Exception {
        UUID bannerId = UUID.randomUUID();
        doNothing().when(bannerService).deleteBanner(bannerId);

        mockMvc.perform(delete("/api/admin/v1/banners/" + bannerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("배너가 삭제되었습니다."));
    }
}
