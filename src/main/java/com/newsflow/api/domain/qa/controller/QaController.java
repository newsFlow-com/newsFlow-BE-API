package com.newsflow.api.domain.qa.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.qa.dto.QaRequest;
import com.newsflow.api.domain.qa.dto.QaResponse;
import com.newsflow.api.domain.qa.service.QaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QA", description = "뉴스 기반 대화형 Q&A (RAG) API")
@RestController
@RequestMapping("/api/v1/qa")
@RequiredArgsConstructor
public class QaController {

    private final QaService qaService;

    @Operation(summary = "뉴스 기반 질의응답",
            description = "최근 기사를 근거로 질문에 답변한다. 근거 기사가 없으면 답변을 생성하지 않고 안내 메시지를 반환한다.")
    @PostMapping
    public ResponseEntity<ApiResponse<QaResponse>> askQuestion(
            @Valid @RequestBody QaRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(qaService.askQuestion(request.question())));
    }
}
