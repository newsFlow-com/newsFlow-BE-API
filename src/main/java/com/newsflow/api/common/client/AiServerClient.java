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

    private static final String QA_FALLBACK_ANSWER =
            "지금은 답변을 생성할 수 없습니다. 잠시 후 다시 시도해주세요.";

    public record QaResult(String answer, List<QaSourceItem> sources) {
        public static QaResult fallback() {
            return new QaResult(QA_FALLBACK_ANSWER, List.of());
        }

        public record QaSourceItem(String articleId, String title) {}
    }

    /**
     * BE-AI POST /qa 호출 → RAG 기반 답변 + 출처 기사 목록 반환.
     * BE-AI 가 응답하지 않으면 fallback 안내 메시지 반환 (graceful fallback).
     */
    @SuppressWarnings("unchecked")
    public QaResult askQuestion(String question) {
        try {
            Map<?, ?> response = webClient.post()
                    .uri("/qa")
                    .bodyValue(Map.of("question", question))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null) return QaResult.fallback();

            String answer = (String) response.get("answer");
            List<Map<String, Object>> sources = (List<Map<String, Object>>) response.get("sources");
            List<QaResult.QaSourceItem> sourceItems = sources == null
                    ? List.of()
                    : sources.stream()
                        .map(s -> new QaResult.QaSourceItem(
                                (String) s.get("article_id"), (String) s.get("title")))
                        .toList();

            return new QaResult(answer != null ? answer : QA_FALLBACK_ANSWER, sourceItems);
        } catch (Exception e) {
            log.warn("BE-AI QA API 호출 실패: {}", e.getMessage());
            return QaResult.fallback();
        }
    }
}
