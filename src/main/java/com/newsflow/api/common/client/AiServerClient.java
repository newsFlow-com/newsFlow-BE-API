package com.newsflow.api.common.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class AiServerClient {

    private final WebClient webClient;

    public AiServerClient(@Value("${ai.server.url}") String aiServerUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(aiServerUrl)
                .build();
    }

    /**
     * BE-AI /recommend/{userId} 호출 → 추천 기사 ID 목록 반환 (점수 내림차순).
     * BE-AI 가 응답하지 않으면 빈 리스트 반환 (graceful fallback).
     */
    @SuppressWarnings("unchecked")
    public List<String> getRecommendedArticleIds(UUID userId, int size) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri(u -> u.path("/recommend/{userId}")
                            .queryParam("size", size)
                            .build(userId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response == null) return List.of();
            List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");
            if (articles == null) return List.of();

            return articles.stream()
                    .map(a -> (String) a.get("article_id"))
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.warn("BE-AI 추천 API 호출 실패 (userId={}): {}", userId, e.getMessage());
            return List.of();
        }
    }
}
