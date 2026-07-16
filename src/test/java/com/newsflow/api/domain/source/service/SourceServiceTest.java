package com.newsflow.api.domain.source.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.domain.source.repository.SourceRepository;
import com.newsflow.api.domain.source.repository.SourceSentimentStatRepository;
import com.newsflow.api.entity.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SourceServiceTest {

    @InjectMocks SourceService sourceService;
    @Mock SourceRepository sourceRepository;
    @Mock SourceSentimentStatRepository sourceSentimentStatRepository;

    @Test
    @DisplayName("카테고리별 감성 통계를 합산해 전체 카운트를 반환한다")
    void getSentimentStats_aggregatesTotals() {
        UUID sourceId = UUID.randomUUID();
        Source source = mock(Source.class);
        when(source.getId()).thenReturn(sourceId);
        when(source.getName()).thenReturn("조선일보");
        when(sourceRepository.findById(sourceId)).thenReturn(Optional.of(source));

        List<Object[]> rows = List.of(
                new Object[]{"economy", "경제", 10L, 3L, 2L},
                new Object[]{"politics", "정치", 5L, 8L, 1L}
        );
        when(sourceSentimentStatRepository.findCategoryBreakdown(eq(sourceId), any(), any()))
                .thenReturn(rows);

        var result = sourceService.getSentimentStats(sourceId, 30);

        assertThat(result.getPositiveCount()).isEqualTo(15);
        assertThat(result.getNegativeCount()).isEqualTo(11);
        assertThat(result.getNeutralCount()).isEqualTo(3);
        assertThat(result.getTotalCount()).isEqualTo(29);
        assertThat(result.getByCategory()).hasSize(2);
    }

    @Test
    @DisplayName("데이터가 없는 매체는 0으로 채워진 응답을 반환한다")
    void getSentimentStats_returnsZerosWhenNoData() {
        UUID sourceId = UUID.randomUUID();
        Source source = mock(Source.class);
        when(source.getId()).thenReturn(sourceId);
        when(source.getName()).thenReturn("신생매체");
        when(sourceRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(sourceSentimentStatRepository.findCategoryBreakdown(eq(sourceId), any(), any()))
                .thenReturn(List.of());

        var result = sourceService.getSentimentStats(sourceId, 30);

        assertThat(result.getTotalCount()).isZero();
        assertThat(result.getByCategory()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 매체 조회 시 BusinessException 발생")
    void getSentimentStats_throwsWhenSourceNotFound() {
        UUID sourceId = UUID.randomUUID();
        when(sourceRepository.findById(sourceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sourceService.getSentimentStats(sourceId, 30))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("from~to 기간이 요청한 days 만큼 계산된다")
    void getSentimentStats_computesDateRange() {
        UUID sourceId = UUID.randomUUID();
        Source source = mock(Source.class);
        when(source.getId()).thenReturn(sourceId);
        when(source.getName()).thenReturn("매체");
        when(sourceRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(sourceSentimentStatRepository.findCategoryBreakdown(eq(sourceId), any(), any()))
                .thenReturn(List.of());

        var result = sourceService.getSentimentStats(sourceId, 7);

        assertThat(LocalDate.parse(result.getTo())).isEqualTo(LocalDate.now());
        assertThat(LocalDate.parse(result.getFrom())).isEqualTo(LocalDate.now().minusDays(7));
    }
}
