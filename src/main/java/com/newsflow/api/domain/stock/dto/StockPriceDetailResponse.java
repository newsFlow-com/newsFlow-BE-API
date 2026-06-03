package com.newsflow.api.domain.stock.dto;

import com.newsflow.api.entity.StockPrice;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class StockPriceDetailResponse {

    private final LocalDate priceDate;
    private final BigDecimal openPrice;
    private final BigDecimal closePrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final Long volume;
    private final BigDecimal changeRate;

    public static StockPriceDetailResponse from(StockPrice price) {
        return StockPriceDetailResponse.builder()
                .priceDate(price.getPriceDate())
                .openPrice(price.getOpenPrice())
                .closePrice(price.getClosePrice())
                .highPrice(price.getHighPrice())
                .lowPrice(price.getLowPrice())
                .volume(price.getVolume())
                .changeRate(price.getChangeRate())
                .build();
    }
}