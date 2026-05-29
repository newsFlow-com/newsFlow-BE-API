package com.newsflow.api.domain.article.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleListRequest {

    private String category;
    private String keyword;
    private String cursor;
    private int size = 20;
}