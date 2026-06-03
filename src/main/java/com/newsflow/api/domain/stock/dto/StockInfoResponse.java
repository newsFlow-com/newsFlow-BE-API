package com.newsflow.api.domain.stock.dto;

import com.newsflow.api.entity.Stock;
import com.newsflow.api.entity.StockPrice;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class StockInfoResponse {

    private final UUID id;
    private final String ticker;
    private final String name;
    private final String market;
    private final String countryCode;
    private final String sector;

    // 최신 가격 정보 (선택 사항)
    private final BigDecimal closePrice;
    private final BigDecimal changeRate;
    private final LocalDate priceDate;

    public static StockInfoResponse from(Stock stock) {
        return from(stock, null);
    }

    public static StockInfoResponse from(Stock stock, StockPrice latestPrice) {
        StockInfoResponseBuilder builder = StockInfoResponse.builder()
                .id(stock.getId())
                .ticker(stock.getTicker())
                .name(stock.getName())
                .market(stock.getMarket())
                .countryCode(stock.getCountryCode())
                .sector(stock.getSector());

        if (latestPrice != null) {
            builder.closePrice(latestPrice.getClosePrice())
                    .changeRate(latestPrice.getChangeRate())
                    .priceDate(latestPrice.getPriceDate());
        }

        return builder.build();
    }
}