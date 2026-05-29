package com.newsflow.api.domain.stats.service;

import com.newsflow.api.domain.stats.dto.*;
import com.newsflow.api.domain.stats.repository.DailyArticleStatRepository;
import com.newsflow.api.entity.DailyArticleStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private static final int REPORT_TOP_N = 10;
    private final DailyArticleStatRepository statRepository;

    public CalendarMonthResponse getCalendarMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<Object[]> rows = statRepository.findDailySummary(from, to);
        List<CalendarDaySummary> days = rows.stream()
                .map(CalendarDaySummary::from)
                .toList();

        return CalendarMonthResponse.builder()
                .year(year)
                .month(month)
                .days(days)
                .build();
    }

    public DailyArticleResponse getDailyArticles(LocalDate date, int size) {
        size = Math.min(size, 50);
        List<DailyArticleStat> stats = statRepository.findByStatDateWithDetails(
                date, PageRequest.of(0, size)
        );

        List<DailyArticleResponse.DailyArticleItem> items = stats.stream()
                .map(DailyArticleResponse.DailyArticleItem::from)
                .toList();

        return DailyArticleResponse.builder()
                .date(date)
                .totalCount(items.size())
                .articles(items)
                .build();
    }

    public MonthlyReportResponse getMonthlyReport(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<DailyArticleStat> stats = statRepository.findTopByMonthWithDetails(
                start, end, PageRequest.of(0, REPORT_TOP_N)
        );

        return MonthlyReportResponse.builder()
                .year(year)
                .month(month)
                .topArticles(stats.stream()
                        .map(DailyArticleResponse.DailyArticleItem::from)
                        .toList())
                .build();
    }

    public YearlyReportResponse getYearlyReport(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<DailyArticleStat> stats = statRepository.findTopByMonthWithDetails(
                start, end, PageRequest.of(0, REPORT_TOP_N)
        );

        return YearlyReportResponse.builder()
                .year(year)
                .topArticles(stats.stream()
                        .map(DailyArticleResponse.DailyArticleItem::from)
                        .toList())
                .build();
    }
}