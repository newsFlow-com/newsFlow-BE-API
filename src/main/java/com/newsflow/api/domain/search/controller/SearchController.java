package com.newsflow.api.domain.search.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.search.dto.SearchArticleResponse;
import com.newsflow.api.domain.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Search", description = "Elasticsearch 전문 검색 API")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "기사 전문 검색",
            description = "제목/요약/본문 멀티매치 검색. 오타 교정(fuzziness) 및 하이라이트 지원.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> search(
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (q == null || q.isBlank()) {
            Map<String, Object> empty = Map.of("total", 0L, "articles", List.of());
            return ResponseEntity.ok(ApiResponse.ok(empty));
        }

        SearchService.SearchResult result = searchService.search(q.trim(), category, page, size);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("total", result.total());
        body.put("page", page);
        body.put("size", size);
        body.put("articles", result.articles());

        return ResponseEntity.ok(ApiResponse.ok(body));
    }
}
