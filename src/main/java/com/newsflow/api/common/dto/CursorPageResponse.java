package com.newsflow.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorPageResponse<T> {

    private final List<T> content;
    private final boolean hasNext;
    private final String nextCursor;  // 다음 페이지 커서 (마지막 항목의 ID 또는 기준값)
    private final int size;

    private CursorPageResponse(List<T> content, boolean hasNext, String nextCursor) {
        this.content = content;
        this.hasNext = hasNext;
        this.nextCursor = hasNext ? nextCursor : null;
        this.size = content.size();
    }

    public static <T> CursorPageResponse<T> of(List<T> content, boolean hasNext, String nextCursor) {
        return new CursorPageResponse<>(content, hasNext, nextCursor);
    }

    /**
     * content 에서 size+1 개를 조회한 결과를 받아
     * hasNext 여부와 nextCursor 를 자동으로 계산한다.
     *
     * @param rawContent size+1 개 조회 결과
     * @param size       요청 페이지 크기
     * @param cursorExtractor 마지막 항목에서 cursor 문자열 추출 함수
     */
    public static <T> CursorPageResponse<T> of(
            List<T> rawContent, int size,
            java.util.function.Function<T, String> cursorExtractor) {

        boolean hasNext = rawContent.size() > size;
        List<T> content = hasNext ? rawContent.subList(0, size) : rawContent;
        String nextCursor = hasNext ? cursorExtractor.apply(content.get(content.size() - 1)) : null;
        return new CursorPageResponse<>(content, hasNext, nextCursor);
    }
}