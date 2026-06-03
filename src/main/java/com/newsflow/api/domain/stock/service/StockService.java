package com.newsflow.api.domain.stock.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.stock.dto.StockChartResponse;
import com.newsflow.api.domain.stock.dto.StockInfoResponse;
import com.newsflow.api.domain.stock.dto.StockLinkedArticleResponse;
import com.newsflow.api.domain.stock.dto.StockPriceDetailResponse;
import com.newsflow.api.domain.stock.repository.ArticleStockRepository;
import com.newsflow.api.domain.stock.repository.StockPriceRepository;
import com.newsflow.api.domain.stock.repository.StockRepository;
import com.newsflow.api.entity.Stock;
import com.newsflow.api.entity.StockPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private static final int DEFAULT_ARTICLE_SIZE = 5;

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final ArticleStockRepository articleStockRepository;

    // ── 종목 검색 ─────────────────────────────────────────────────

    public List<StockInfoResponse> searchStocks(String keyword, int size) {
        return stockRepository.searchByKeyword(keyword, PageRequest.of(0, size))
                .stream()
                .map(stock -> {
                    StockPrice latest = stockPriceRepository
                            .findLatestByStockId(stock.getId(), PageRequest.of(0, 1))
                            .stream().findFirst().orElse(null);
                    return StockInfoResponse.from(stock, latest);
                })
                .toList();
    }

    // ── 시장별 종목 목록 ──────────────────────────────────────────

    public List<StockInfoResponse> getStocksByMarket(String market) {
        return stockRepository.findByMarket(market)
                .stream()
                .map(stock -> {
                    StockPrice latest = stockPriceRepository
                            .findLatestByStockId(stock.getId(), PageRequest.of(0, 1))
                            .stream().findFirst().orElse(null);
                    return StockInfoResponse.from(stock, latest);
                })
                .toList();
    }

    // ── 종목 차트 데이터 조회 ─────────────────────────────────────

    /**
     * 종목 ID 기준 가격 이력 + 연관 기사 반환.
     * FE 차트 시각화에서 사용.
     */
    public StockChartResponse getStockChart(UUID stockId, int days) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDate from = LocalDate.now().minusDays(days);

        // 변경된 StockPriceDetailResponse 활용
        List<StockPriceDetailResponse> prices = stockPriceRepository
                .findByStockIdAndDateRange(stockId, from)
                .stream()
                .map(StockPriceDetailResponse::from)
                .toList();

        // StockChartResponse 내부 static 구조에 맞게 매핑
        List<StockChartResponse.ChartArticleItem> recentArticles =
                articleStockRepository.findByStockId(stockId, PageRequest.of(0, DEFAULT_ARTICLE_SIZE))
                        .stream()
                        .map(as -> StockChartResponse.ChartArticleItem.builder()
                                .articleId(as.getArticle().getId())
                                .title(as.getArticle().getTitle())
                                .publishedAt(as.getArticle().getPublishedAt() != null
                                        ? as.getArticle().getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                        : null)
                                .mentionScore(as.getMentionScore())
                                .build())
                        .toList();

        return StockChartResponse.builder()
                .stockId(stock.getId())
                .ticker(stock.getTicker())
                .name(stock.getName())
                .market(stock.getMarket())
                .sector(stock.getSector())
                .prices(prices)
                .recentArticles(recentArticles)
                .build();
    }

    // ── 기사 연관 종목 조회 ───────────────────────────────────────

    /**
     * 기사 ID로 연관 종목 목록 조회.
     */
    public List<StockLinkedArticleResponse> getStocksByArticle(UUID articleId) {
        return articleStockRepository.findByArticleId(articleId)
                .stream()
                .map(StockLinkedArticleResponse::from)
                .toList();
    }

    // ── 티커로 종목 조회 ──────────────────────────────────────────

    public StockInfoResponse getStockByTicker(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker.toUpperCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        StockPrice latest = stockPriceRepository
                .findLatestByStockId(stock.getId(), PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);

        return StockInfoResponse.from(stock, latest);
    }
}