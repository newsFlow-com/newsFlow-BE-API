package com.newsflow.api.domain.stock.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class StockChartResponse {

    private final UUID stockId;
    private final String ticker;
    private final String name;
    private final String market;
    private final String sector;

    /** 분리된 일별 가격 상세 DTO 활용 */
    private final List<StockPriceDetailResponse> prices;

    /** 이 종목을 언급한 최근 기사 목록 */
    private final List<ChartArticleItem> recentArticles;

    @Getter
    @Builder
    public static class ChartArticleItem {
        private final UUID articleId;
        private final String title;
        private final String publishedAt;
        private final Double mentionScore;
        private final String sentiment;

        /** 발행일 기준 등락률(%) — 인과관계가 아닌 발행 시점 전후 주가 동조화 참고 지표 */
        private final Double priceChangePublishDay;
        /** 발행일 종가 → 3거래일 후 종가까지의 누적 변동률(%) */
        private final Double priceChange3d;
    }
}