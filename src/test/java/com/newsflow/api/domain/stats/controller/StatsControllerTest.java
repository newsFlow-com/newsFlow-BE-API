package com.newsflow.api.domain.stats.controller;

import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.log.repository.ApiRequestLogRepository;
import com.newsflow.api.domain.stats.dto.WeeklyReportResponse;
import com.newsflow.api.domain.stats.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = StatsController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class StatsControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean StatsService statsService;
    @MockBean JwtUtil jwtUtil;
    @MockBean ApiRequestLogRepository apiRequestLogRepository;

    @Test
    @DisplayName("GET /api/v1/stats/report/weekly — year, week 파라미터로 주별 리포트를 반환한다")
    void getWeeklyReport_returnsOk() throws Exception {
        WeeklyReportResponse response = WeeklyReportResponse.builder()
                .year(2025)
                .week(10)
                .startDate(LocalDate.of(2025, 3, 3))
                .endDate(LocalDate.of(2025, 3, 9))
                .topArticles(List.of())
                .build();
        when(statsService.getWeeklyReport(2025, 10)).thenReturn(response);

        mockMvc.perform(get("/api/v1/stats/report/weekly")
                        .param("year", "2025")
                        .param("week", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.week").value(10))
                .andExpect(jsonPath("$.data.startDate").value("2025-03-03"))
                .andExpect(jsonPath("$.data.endDate").value("2025-03-09"));
    }

    @Test
    @DisplayName("GET /api/v1/stats/report/weekly — year 파라미터 누락 시 400 반환")
    void getWeeklyReport_returns400_whenYearMissing() throws Exception {
        mockMvc.perform(get("/api/v1/stats/report/weekly")
                        .param("week", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/stats/report/weekly — week 파라미터 누락 시 400 반환")
    void getWeeklyReport_returns400_whenWeekMissing() throws Exception {
        mockMvc.perform(get("/api/v1/stats/report/weekly")
                        .param("year", "2025"))
                .andExpect(status().isBadRequest());
    }
}
