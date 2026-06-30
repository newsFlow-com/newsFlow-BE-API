package com.newsflow.api.domain.stats.service;

import com.newsflow.api.domain.stats.dto.WeeklyReportResponse;
import com.newsflow.api.domain.stats.repository.DailyArticleStatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @InjectMocks StatsService statsService;
    @Mock DailyArticleStatRepository statRepository;

    @Test
    @DisplayName("주별 리포트는 year/week/startDate/endDate를 올바르게 반환한다")
    void getWeeklyReport_returnsCorrectFields() {
        when(statRepository.findTopByMonthWithDetails(any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        WeeklyReportResponse response = statsService.getWeeklyReport(2025, 2);

        assertThat(response.getYear()).isEqualTo(2025);
        assertThat(response.getWeek()).isEqualTo(2);
        assertThat(response.getTopArticles()).isEmpty();
    }

    @Test
    @DisplayName("주별 리포트 startDate는 월요일, endDate는 일요일이다")
    void getWeeklyReport_dateRangeIsMonToSun() {
        when(statRepository.findTopByMonthWithDetails(any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        WeeklyReportResponse response = statsService.getWeeklyReport(2025, 10);

        assertThat(response.getStartDate().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(response.getEndDate().getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
        assertThat(response.getEndDate()).isEqualTo(response.getStartDate().plusDays(6));
    }

    @Test
    @DisplayName("주별 리포트는 올바른 날짜 범위로 Repository를 호출한다")
    void getWeeklyReport_passesCorrectDateRangeToRepository() {
        when(statRepository.findTopByMonthWithDetails(any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        // ISO 2025년 2주차: 2025-01-06(월) ~ 2025-01-12(일)
        LocalDate expectedStart = LocalDate.of(2025, 1, 6);
        LocalDate expectedEnd = LocalDate.of(2025, 1, 12);

        statsService.getWeeklyReport(2025, 2);

        verify(statRepository).findTopByMonthWithDetails(
                eq(expectedStart), eq(expectedEnd), any(Pageable.class));
    }

    @Test
    @DisplayName("주별 리포트 startDate의 ISO 주차가 요청 week와 일치한다")
    void getWeeklyReport_isoWeekNumberMatchesRequest() {
        when(statRepository.findTopByMonthWithDetails(any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        int requestedWeek = 15;
        WeeklyReportResponse response = statsService.getWeeklyReport(2025, requestedWeek);

        int actualWeek = response.getStartDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        assertThat(actualWeek).isEqualTo(requestedWeek);
    }
}
