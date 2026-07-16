package com.newsflow.api.domain.source.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.source.dto.SourceResponse;
import com.newsflow.api.domain.source.dto.SourceSentimentStatResponse;
import com.newsflow.api.domain.source.repository.SourceRepository;
import com.newsflow.api.domain.source.repository.SourceSentimentStatRepository;
import com.newsflow.api.entity.Source;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SourceService {

    private final SourceRepository sourceRepository;
    private final SourceSentimentStatRepository sourceSentimentStatRepository;

    public List<SourceResponse> getSources() {
        return sourceRepository.findByIsActiveTrueOrderByNameAsc()
                .stream().map(SourceResponse::from).toList();
    }

    public SourceSentimentStatResponse getSentimentStats(UUID sourceId, int days) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(Math.max(days, 1));

        List<Object[]> rows = sourceSentimentStatRepository.findCategoryBreakdown(sourceId, from, to);

        long totalPositive = 0, totalNegative = 0, totalNeutral = 0;
        var byCategory = new java.util.ArrayList<SourceSentimentStatResponse.CategoryBreakdown>();

        for (Object[] row : rows) {
            long positive = ((Number) row[2]).longValue();
            long negative = ((Number) row[3]).longValue();
            long neutral = ((Number) row[4]).longValue();

            totalPositive += positive;
            totalNegative += negative;
            totalNeutral += neutral;

            byCategory.add(SourceSentimentStatResponse.CategoryBreakdown.builder()
                    .categorySlug((String) row[0])
                    .categoryName((String) row[1])
                    .positiveCount(positive)
                    .negativeCount(negative)
                    .neutralCount(neutral)
                    .build());
        }

        return SourceSentimentStatResponse.builder()
                .sourceId(source.getId())
                .sourceName(source.getName())
                .from(from.toString())
                .to(to.toString())
                .positiveCount(totalPositive)
                .negativeCount(totalNegative)
                .neutralCount(totalNeutral)
                .totalCount(totalPositive + totalNegative + totalNeutral)
                .byCategory(byCategory)
                .build();
    }
}
