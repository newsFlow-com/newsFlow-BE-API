package com.newsflow.api.domain.stock.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.domain.stock.repository.ArticleStockRepository;
import com.newsflow.api.domain.stock.repository.StockPriceRepository;
import com.newsflow.api.domain.stock.repository.StockRepository;
import com.newsflow.api.entity.Article;
import com.newsflow.api.entity.ArticleStock;
import com.newsflow.api.entity.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks StockService stockService;
    @Mock StockRepository stockRepository;
    @Mock StockPriceRepository stockPriceRepository;
    @Mock ArticleStockRepository articleStockRepository;

    private Stock mockStock(UUID id) {
        Stock stock = mock(Stock.class);
        when(stock.getId()).thenReturn(id);
        when(stock.getTicker()).thenReturn("005930");
        when(stock.getName()).thenReturn("삼성전자");
        when(stock.getMarket()).thenReturn("KOSPI");
        return stock;
    }

    @Test
    @DisplayName("관련 기사의 감성이 ChartArticleItem에 그대로 매핑된다")
    void getStockChart_mapsArticleSentiment() {
        UUID stockId = UUID.randomUUID();
        Stock stock = mockStock(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockPriceRepository.findByStockIdAndDateRange(eq(stockId), any())).thenReturn(List.of());

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(UUID.randomUUID());
        when(article.getTitle()).thenReturn("삼성전자 실적 발표");
        when(article.getSentiment()).thenReturn("positive");

        ArticleStock articleStock = mock(ArticleStock.class);
        when(articleStock.getArticle()).thenReturn(article);
        when(articleStock.getMentionScore()).thenReturn(0.9);

        when(articleStockRepository.findByStockId(eq(stockId), any(Pageable.class)))
                .thenReturn(List.of(articleStock));

        var result = stockService.getStockChart(stockId, 30, 20);

        assertThat(result.getRecentArticles()).hasSize(1);
        assertThat(result.getRecentArticles().get(0).getSentiment()).isEqualTo("positive");
    }

    @Test
    @DisplayName("영향도 필드(priceChangePublishDay, priceChange3d)가 그대로 매핑된다")
    void getStockChart_mapsImpactFields() {
        UUID stockId = UUID.randomUUID();
        Stock stock = mockStock(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockPriceRepository.findByStockIdAndDateRange(eq(stockId), any())).thenReturn(List.of());

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(UUID.randomUUID());
        when(article.getTitle()).thenReturn("삼성전자 실적 발표");

        ArticleStock articleStock = mock(ArticleStock.class);
        when(articleStock.getArticle()).thenReturn(article);
        when(articleStock.getPriceChangePublishDay()).thenReturn(1.5);
        when(articleStock.getPriceChange3d()).thenReturn(10.0);

        when(articleStockRepository.findByStockId(eq(stockId), any(Pageable.class)))
                .thenReturn(List.of(articleStock));

        var result = stockService.getStockChart(stockId, 30, 20);

        var item = result.getRecentArticles().get(0);
        assertThat(item.getPriceChangePublishDay()).isEqualTo(1.5);
        assertThat(item.getPriceChange3d()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("아직 영향도 분석이 안 된 기사는 null로 반환된다")
    void getStockChart_returnsNullWhenImpactNotAnalyzedYet() {
        UUID stockId = UUID.randomUUID();
        Stock stock = mockStock(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockPriceRepository.findByStockIdAndDateRange(eq(stockId), any())).thenReturn(List.of());

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(UUID.randomUUID());
        when(article.getTitle()).thenReturn("삼성전자 실적 발표");

        ArticleStock articleStock = mock(ArticleStock.class);
        when(articleStock.getArticle()).thenReturn(article);
        when(articleStock.getPriceChangePublishDay()).thenReturn(null);
        when(articleStock.getPriceChange3d()).thenReturn(null);

        when(articleStockRepository.findByStockId(eq(stockId), any(Pageable.class)))
                .thenReturn(List.of(articleStock));

        var result = stockService.getStockChart(stockId, 30, 20);

        var item = result.getRecentArticles().get(0);
        assertThat(item.getPriceChangePublishDay()).isNull();
        assertThat(item.getPriceChange3d()).isNull();
    }

    @Test
    @DisplayName("articleLimit이 MAX(50)를 넘으면 50으로 제한된다")
    void getStockChart_capsArticleLimitAtMax() {
        UUID stockId = UUID.randomUUID();
        Stock stock = mockStock(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockPriceRepository.findByStockIdAndDateRange(eq(stockId), any())).thenReturn(List.of());
        when(articleStockRepository.findByStockId(eq(stockId), any(Pageable.class))).thenReturn(List.of());

        stockService.getStockChart(stockId, 30, 999);

        verify(articleStockRepository).findByStockId(
                eq(stockId), argThat(p -> p.getPageSize() == 50));
    }

    @Test
    @DisplayName("articleLimit 미지정 시 기본값 20이 사용된다")
    void getStockChart_defaultsArticleLimitTo20() {
        UUID stockId = UUID.randomUUID();
        Stock stock = mockStock(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));
        when(stockPriceRepository.findByStockIdAndDateRange(eq(stockId), any())).thenReturn(List.of());
        when(articleStockRepository.findByStockId(eq(stockId), any(Pageable.class))).thenReturn(List.of());

        stockService.getStockChart(stockId, 30);

        verify(articleStockRepository).findByStockId(
                eq(stockId), argThat(p -> p.getPageSize() == 20));
    }

    @Test
    @DisplayName("존재하지 않는 종목 조회 시 BusinessException 발생")
    void getStockChart_throwsWhenStockNotFound() {
        UUID stockId = UUID.randomUUID();
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.getStockChart(stockId, 30))
                .isInstanceOf(BusinessException.class);
    }
}
