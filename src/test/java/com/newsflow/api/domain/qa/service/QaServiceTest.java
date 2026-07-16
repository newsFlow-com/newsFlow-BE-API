package com.newsflow.api.domain.qa.service;

import com.newsflow.api.common.client.AiServerClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QaServiceTest {

    @InjectMocks QaService qaService;
    @Mock AiServerClient aiServerClient;

    @Test
    @DisplayName("BE-AI 응답이 answer/sources 그대로 매핑된다")
    void askQuestion_mapsAiServerResponse() {
        var qaResult = new AiServerClient.QaResult(
                "삼성전자는 최근 실적이 좋습니다.",
                List.of(new AiServerClient.QaResult.QaSourceItem("a1", "삼성전자 실적 발표"))
        );
        when(aiServerClient.askQuestion("삼성전자 실적 어때?")).thenReturn(qaResult);

        var result = qaService.askQuestion("삼성전자 실적 어때?");

        assertThat(result.getAnswer()).isEqualTo("삼성전자는 최근 실적이 좋습니다.");
        assertThat(result.getSources()).hasSize(1);
        assertThat(result.getSources().get(0).getArticleId()).isEqualTo("a1");
        verify(aiServerClient).askQuestion("삼성전자 실적 어때?");
    }

    @Test
    @DisplayName("출처가 없는 응답(fallback)도 빈 리스트로 정상 매핑된다")
    void askQuestion_mapsEmptySources() {
        var qaResult = AiServerClient.QaResult.fallback();
        when(aiServerClient.askQuestion("질문")).thenReturn(qaResult);

        var result = qaService.askQuestion("질문");

        assertThat(result.getSources()).isEmpty();
        assertThat(result.getAnswer()).isNotBlank();
    }
}
