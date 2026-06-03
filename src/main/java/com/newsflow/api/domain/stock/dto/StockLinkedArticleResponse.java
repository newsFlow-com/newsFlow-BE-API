package com.newsflow.api.domain.stock.dto;

import com.newsflow.api.entity.ArticleStock;
import com.newsflow.api.entity.Stock;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class StockLinkedArticleResponse {

    private final UUID stockId;
    private final String ticker;
    private final String name;
    private final String market;
    private final Double mentionScore;
    private final String linkedBy;

    public static StockLinkedArticleResponse from(ArticleStock articleStock) {
        Stock stock = articleStock.getStock();
        return StockLinkedArticleResponse.builder()
                .stockId(stock.getId())
                .ticker(stock.getTicker())
                .name(stock.getName())
                .market(stock.getMarket())
                .mentionScore(articleStock.getMentionScore())
                .linkedBy(articleStock.getLinkedBy())
                .build();
    }
}