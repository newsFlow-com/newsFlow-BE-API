package com.newsflow.api.domain.stock.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.stock.dto.StockChartResponse;
import com.newsflow.api.domain.stock.dto.StockInfoResponse;
import com.newsflow.api.domain.stock.dto.StockLinkedArticleResponse;
import com.newsflow.api.domain.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Stock", description = "주식 연계 API — 종목 조회, 차트 데이터, 기사-종목 연관")
@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "종목 검색", description = "종목명 또는 티커로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StockInfoResponse>>> searchStocks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(stockService.searchStocks(keyword, size))
        );
    }

    @Operation(summary = "시장별 종목 목록", description = "KOSPI | KOSDAQ | NYSE | NASDAQ 별 종목 목록을 조회합니다.")
    @GetMapping("/market/{market}")
    public ResponseEntity<ApiResponse<List<StockInfoResponse>>> getStocksByMarket(
            @Parameter(description = "시장 코드: KOSPI | KOSDAQ | NYSE | NASDAQ")
            @PathVariable String market
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(stockService.getStocksByMarket(market))
        );
    }

    @Operation(summary = "티커로 종목 조회", description = "티커 심볼로 종목 정보와 최신 가격을 조회합니다.")
    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<ApiResponse<StockInfoResponse>> getStockByTicker(
            @PathVariable String ticker
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(stockService.getStockByTicker(ticker))
        );
    }

    @Operation(summary = "종목 차트 데이터", description = "종목의 OHLCV 가격 이력과 연관 기사를 반환합니다. FE 차트 시각화에서 사용합니다.")
    @GetMapping("/{stockId}/chart")
    public ResponseEntity<ApiResponse<StockChartResponse>> getStockChart(
            @PathVariable UUID stockId,
            @Parameter(description = "조회 기간 (일, 기본 30일)")
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(stockService.getStockChart(stockId, days))
        );
    }

    @Operation(summary = "기사 연관 종목 목록", description = "특정 기사에 언급된 종목 목록을 mention_score 내림차순으로 반환합니다.")
    @GetMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<List<StockLinkedArticleResponse>>> getStocksByArticle(
            @PathVariable UUID articleId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(stockService.getStocksByArticle(articleId))
        );
    }
}