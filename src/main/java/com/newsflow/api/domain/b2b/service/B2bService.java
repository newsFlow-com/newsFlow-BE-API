package com.newsflow.api.domain.b2b.service;

import com.newsflow.api.domain.b2b.dto.B2bDailyStatResponse;
import com.newsflow.api.domain.b2b.dto.B2bKeywordStatResponse;
import com.newsflow.api.domain.b2b.dto.B2bSentimentStatResponse;
import com.newsflow.api.domain.b2b.repository.B2bStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class B2bService {

    private final B2bStatsRepository b2bStatsRepository;

    public B2bDailyStatResponse getDailyStats(LocalDate from, LocalDate to, String category) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        List<Object[]> rows = b2bStatsRepository.findDailyStats(fromDt, toDt,
                (category != null && !category.isBlank()) ? category : null);

        Map<String, List<B2bDailyStatResponse.CategoryCount>> byDate = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String date = (String) row[0];
            var entry = B2bDailyStatResponse.CategoryCount.builder()
                    .slug((String) row[1])
                    .name((String) row[2])
                    .count(((Number) row[3]).longValue())
                    .build();
            byDate.computeIfAbsent(date, k -> new ArrayList<>()).add(entry);
        }

        List<B2bDailyStatResponse.DayEntry> data = byDate.entrySet().stream()
                .map(e -> B2bDailyStatResponse.DayEntry.builder()
                        .date(e.getKey())
                        .categories(e.getValue())
                        .build())
                .toList();

        return B2bDailyStatResponse.builder()
                .from(from.toString())
                .to(to.toString())
                .data(data)
                .build();
    }

    public B2bSentimentStatResponse getSentimentStats(LocalDate from, LocalDate to, String category) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();
        String cat = (category != null && !category.isBlank()) ? category : null;

        List<Object[]> rows = b2bStatsRepository.findSentimentStats(fromDt, toDt, cat);

        Map<String, long[]> byDate = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String date = (String) row[0];
            String sent = (String) row[1];
            long cnt = ((Number) row[2]).longValue();

            long[] counts = byDate.computeIfAbsent(date, k -> new long[3]);
            if ("positive".equals(sent)) counts[0] += cnt;
            else if ("negative".equals(sent)) counts[1] += cnt;
            else counts[2] += cnt;
        }

        List<B2bSentimentStatResponse.DaySentiment> data = byDate.entrySet().stream()
                .map(e -> {
                    long[] c = e.getValue();
                    return B2bSentimentStatResponse.DaySentiment.builder()
                            .date(e.getKey())
                            .positive(c[0])
                            .negative(c[1])
                            .neutral(c[2])
                            .total(c[0] + c[1] + c[2])
                            .build();
                })
                .toList();

        return B2bSentimentStatResponse.builder()
                .from(from.toString())
                .to(to.toString())
                .category(category)
                .data(data)
                .build();
    }

    public B2bKeywordStatResponse getKeywordStats(LocalDate from, LocalDate to, int limit) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atStartOfDay();
        int safeLimit = Math.min(Math.max(limit, 1), 100);

        List<Object[]> rows = b2bStatsRepository.findKeywordStats(fromDt, toDt, safeLimit);

        List<B2bKeywordStatResponse.KeywordEntry> keywords = rows.stream()
                .map(row -> B2bKeywordStatResponse.KeywordEntry.builder()
                        .word((String) row[0])
                        .mentionCount(((Number) row[1]).longValue())
                        .avgRank(((Number) row[2]).doubleValue())
                        .bestRank(((Number) row[3]).intValue())
                        .build())
                .toList();

        return B2bKeywordStatResponse.builder()
                .from(from.toString())
                .to(to.toString())
                .keywords(keywords)
                .build();
    }
}
