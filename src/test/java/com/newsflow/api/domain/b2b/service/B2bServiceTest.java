package com.newsflow.api.domain.b2b.service;

import com.newsflow.api.domain.b2b.repository.B2bStatsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class B2bServiceTest {

    @InjectMocks B2bService b2bService;
    @Mock B2bStatsRepository b2bStatsRepository;

    private static List<Object[]> rows(Object[]... arrays) {
        List<Object[]> list = new ArrayList<>();
        for (Object[] a : arrays) list.add(a);
        return list;
    }

    @Test
    @DisplayName("일별 통계 — DB 결과를 날짜별로 그룹화")
    void getDailyStats_groupsByDate() {
        when(b2bStatsRepository.findDailyStats(any(), any(), any()))
                .thenReturn(rows(
                        new Object[]{"2024-01-01", "economy", "경제", 42L},
                        new Object[]{"2024-01-01", "tech", "테크", 18L},
                        new Object[]{"2024-01-02", "economy", "경제", 30L}
                ));

        var result = b2bService.getDailyStats(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), null);

        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getDate()).isEqualTo("2024-01-01");
        assertThat(result.getData().get(0).getCategories()).hasSize(2);
        assertThat(result.getData().get(0).getCategories().get(0).getCount()).isEqualTo(42L);
    }

    @Test
    @DisplayName("일별 통계 — 빈 결과 처리")
    void getDailyStats_emptyResult() {
        when(b2bStatsRepository.findDailyStats(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        var result = b2bService.getDailyStats(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 7), null);

        assertThat(result.getData()).isEmpty();
        assertThat(result.getFrom()).isEqualTo("2024-01-01");
        assertThat(result.getTo()).isEqualTo("2024-01-07");
    }

    @Test
    @DisplayName("감성 통계 — positive/negative/neutral 집계")
    void getSentimentStats_aggregatesSentiments() {
        when(b2bStatsRepository.findSentimentStats(any(), any(), any()))
                .thenReturn(rows(
                        new Object[]{"2024-01-01", "positive", 30L},
                        new Object[]{"2024-01-01", "negative", 10L},
                        new Object[]{"2024-01-01", "neutral", 60L}
                ));

        var result = b2bService.getSentimentStats(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1), null);

        assertThat(result.getData()).hasSize(1);
        var day = result.getData().get(0);
        assertThat(day.getPositive()).isEqualTo(30L);
        assertThat(day.getNegative()).isEqualTo(10L);
        assertThat(day.getNeutral()).isEqualTo(60L);
        assertThat(day.getTotal()).isEqualTo(100L);
    }

    @Test
    @DisplayName("키워드 통계 — limit 100 클리핑")
    void getKeywordStats_clipsLimit() {
        when(b2bStatsRepository.findKeywordStats(any(), any(), eq(100)))
                .thenReturn(new ArrayList<>());

        var result = b2bService.getKeywordStats(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 7), 9999);

        assertThat(result.getKeywords()).isEmpty();
    }

    @Test
    @DisplayName("키워드 통계 — 결과 매핑")
    void getKeywordStats_mapsFields() {
        when(b2bStatsRepository.findKeywordStats(any(), any(), anyInt()))
                .thenReturn(rows(new Object[]{"삼성", 15L, 2.3, 1}));

        var result = b2bService.getKeywordStats(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 7), 20);

        assertThat(result.getKeywords()).hasSize(1);
        assertThat(result.getKeywords().get(0).getWord()).isEqualTo("삼성");
        assertThat(result.getKeywords().get(0).getMentionCount()).isEqualTo(15L);
        assertThat(result.getKeywords().get(0).getBestRank()).isEqualTo(1);
    }
}
