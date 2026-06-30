package com.newsflow.api.domain.banner.controller;

import com.newsflow.api.common.config.SecurityConfig;
import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.admin.repository.ApiKeyRepository;
import com.newsflow.api.domain.banner.dto.BannerResponse;
import com.newsflow.api.domain.banner.service.BannerService;
import com.newsflow.api.domain.log.repository.ApiRequestLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig의 GET /api/v1/banners/** permitAll 적용 검증.
 * @Import(SecurityConfig.class)로 실제 보안 규칙을 적용해 401이 아닌 200을 검증한다.
 */
@WebMvcTest(BannerController.class)
@Import(SecurityConfig.class)
class BannerPermitAllTest {

    @Autowired MockMvc mockMvc;
    @MockBean BannerService bannerService;
    @MockBean JwtUtil jwtUtil;
    @MockBean ApiRequestLogRepository apiRequestLogRepository;
    @MockBean ApiKeyRepository apiKeyRepository;

    @Test
    @DisplayName("GET /api/v1/banners — 인증 없이 접근해도 200 반환 (401 아님)")
    void getActiveBanners_anonymousAccess_returns200() throws Exception {
        when(bannerService.getActiveBanners(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/banners"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/banners?position=main_top — 인증 없이 position 필터도 200 반환")
    void getActiveBanners_withPosition_anonymousAccess_returns200() throws Exception {
        when(bannerService.getActiveBanners("main_top")).thenReturn(List.of(
                BannerResponse.builder()
                        .id(UUID.randomUUID())
                        .title("메인 배너")
                        .imageUrl("http://img.test/1.jpg")
                        .position("main_top")
                        .displayOrder(1)
                        .build()
        ));

        mockMvc.perform(get("/api/v1/banners").param("position", "main_top"))
                .andExpect(status().isOk());
    }
}
