package com.newsflow.api.domain.qa.service;

import com.newsflow.api.common.client.AiServerClient;
import com.newsflow.api.domain.qa.dto.QaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QaService {

    private final AiServerClient aiServerClient;

    public QaResponse askQuestion(String question) {
        AiServerClient.QaResult result = aiServerClient.askQuestion(question);
        return QaResponse.from(result);
    }
}
