package com.newsflow.api.domain.banner.controller;

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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = BannerController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class BannerControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean BannerService bannerService;
    @MockBean JwtUtil jwtUtil;
    @MockBean ApiRequestLogRepository apiRequestLogRepository;

    @Test
    @DisplayName("GET /api/v1/banners — position 없이 전체 배너 목록을 반환한다")
    void getActiveBanners_withoutPosition_returnsAll() throws Exception {
        BannerResponse banner = BannerResponse.builder()
                .id(UUID.randomUUID())
                .title("메인 배너")
                .imageUrl("http://img.test/1.jpg")
                .position("main_top")
                .displayOrder(1)
                .build();
        when(bannerService.getActiveBanners(null)).thenReturn(List.of(banner));

        mockMvc.perform(get("/api/v1/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("메인 배너"))
                .andExpect(jsonPath("$.data[0].position").value("main_top"));
    }

    @Test
    @DisplayName("GET /api/v1/banners?position=sidebar — position 필터로 배너 조회")
    void getActiveBanners_withPosition_returnsFiltered() throws Exception {
        when(bannerService.getActiveBanners("sidebar")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/banners").param("position", "sidebar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(bannerService).getActiveBanners("sidebar");
    }

    @Test
    @DisplayName("GET /api/v1/banners — 배너가 없으면 빈 배열을 반환한다")
    void getActiveBanners_returnsEmptyList_whenNoBanners() throws Exception {
        when(bannerService.getActiveBanners(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
