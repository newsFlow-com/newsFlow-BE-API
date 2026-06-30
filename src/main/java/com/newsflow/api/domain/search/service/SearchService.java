package com.newsflow.api.domain.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.newsflow.api.domain.search.dto.SearchArticleResponse;
import com.newsflow.api.entity.ArticleDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MAX_SIZE = 50;

    private final ElasticsearchOperations elasticsearchOperations;

    public record SearchResult(long total, List<SearchArticleResponse> articles) {}

    public SearchResult search(String q, String category, int page, int size) {
        size = Math.min(size, MAX_SIZE);

        Query multiMatch = MultiMatchQuery.of(mm -> mm
                .fields("title^3", "summary^2", "ai_summary", "content")
                .query(q)
                .type(TextQueryType.BestFields)
                .fuzziness("AUTO")
        )._toQuery();

        List<Query> filters = new ArrayList<>();
        filters.add(TermQuery.of(t -> t.field("status").value("active"))._toQuery());
        if (category != null && !category.isBlank()) {
            filters.add(TermQuery.of(t -> t.field("categories").value(category))._toQuery());
        }

        Query boolQuery = BoolQuery.of(b -> b
                .must(multiMatch)
                .filter(filters)
        )._toQuery();

        HighlightFieldParameters fieldParams = HighlightFieldParameters.builder()
                .withNumberOfFragments(1)
                .withFragmentSize(150)
                .build();

        Highlight highlight = new Highlight(
                HighlightParameters.builder()
                        .withPreTags("<em>")
                        .withPostTags("</em>")
                        .build(),
                List.of(
                        new HighlightField("title", fieldParams),
                        new HighlightField("summary", fieldParams)
                )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withHighlightQuery(
                        new org.springframework.data.elasticsearch.core.query.HighlightQuery(
                                highlight, ArticleDocument.class))
                .withPageable(PageRequest.of(page, size))
                .build();

        try {
            SearchHits<ArticleDocument> hits = elasticsearchOperations.search(nativeQuery, ArticleDocument.class);
            List<SearchArticleResponse> results = hits.stream()
                    .map(hit -> SearchArticleResponse.from(
                            hit.getContent(),
                            hit.getScore(),
                            hit.getHighlightFields()
                    ))
                    .toList();
            return new SearchResult(hits.getTotalHits(), results);
        } catch (Exception e) {
            log.warn("Elasticsearch 검색 실패 (q={}): {}", q, e.getMessage());
            return new SearchResult(0, List.of());
        }
    }
}
