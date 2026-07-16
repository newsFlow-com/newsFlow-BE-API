package com.newsflow.api.domain.qa.dto;

import com.newsflow.api.common.client.AiServerClient;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QaResponse {

    private String answer;
    private List<QaSource> sources;

    @Getter
    @Builder
    public static class QaSource {
        private String articleId;
        private String title;
    }

    public static QaResponse from(AiServerClient.QaResult result) {
        return QaResponse.builder()
                .answer(result.answer())
                .sources(result.sources().stream()
                        .map(s -> QaSource.builder()
                                .articleId(s.articleId())
                                .title(s.title())
                                .build())
                        .toList())
                .build();
    }
}
