package com.newsflow.api.domain.search.service;

import com.newsflow.api.entity.ArticleDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @InjectMocks SearchService searchService;
    @Mock ElasticsearchOperations elasticsearchOperations;

    @Test
    @DisplayName("검색 결과 반환 — 히트 수와 문서 수 일치")
    @SuppressWarnings("unchecked")
    void search_returnsMatchingDocuments() {
        ArticleDocument doc = ArticleDocument.builder()
                .id("uuid-1")
                .title("삼성 실적 발표")
                .summary("삼성전자 2분기 실적")
                .categories(List.of("economy"))
                .keywords(List.of("삼성", "실적"))
                .build();

        SearchHit<ArticleDocument> hit = mock(SearchHit.class);
        when(hit.getContent()).thenReturn(doc);
        when(hit.getScore()).thenReturn(4.5f);
        when(hit.getHighlightFields()).thenReturn(java.util.Map.of());

        SearchHits<ArticleDocument> hits = mock(SearchHits.class);
        when(hits.getTotalHits()).thenReturn(1L);
        when(hits.stream()).thenReturn(java.util.stream.Stream.of(hit));

        when(elasticsearchOperations.search(any(Query.class), eq(ArticleDocument.class)))
                .thenReturn(hits);

        SearchService.SearchResult result = searchService.search("삼성", null, 0, 10);

        assertThat(result.total()).isEqualTo(1L);
        assertThat(result.articles()).hasSize(1);
        assertThat(result.articles().get(0).getTitle()).isEqualTo("삼성 실적 발표");
        assertThat(result.articles().get(0).getScore()).isEqualTo(4.5f);
    }

    @Test
    @DisplayName("Elasticsearch 장애 시 빈 결과 반환 (graceful fallback)")
    void search_returnEmptyResult_whenElasticsearchFails() {
        when(elasticsearchOperations.search(any(Query.class), eq(ArticleDocument.class)))
                .thenThrow(new RuntimeException("ES 연결 실패"));

        SearchService.SearchResult result = searchService.search("삼성", null, 0, 10);

        assertThat(result.total()).isZero();
        assertThat(result.articles()).isEmpty();
    }

    @Test
    @DisplayName("size 50 초과 시 최대 50으로 클리핑")
    @SuppressWarnings("unchecked")
    void search_clipsMaxSize() {
        SearchHits<ArticleDocument> hits = mock(SearchHits.class);
        when(hits.getTotalHits()).thenReturn(0L);
        when(hits.stream()).thenReturn(java.util.stream.Stream.empty());
        when(elasticsearchOperations.search(any(Query.class), eq(ArticleDocument.class)))
                .thenReturn(hits);

        SearchService.SearchResult result = searchService.search("테스트", null, 0, 200);

        assertThat(result.total()).isZero();
    }
}
