package com.newsflow.api.domain.bookmark.controller;

import com.newsflow.api.common.dto.ApiResponse;
import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.domain.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Bookmark", description = "북마크 API")
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 추가")
    @PostMapping("/{articleId}")
    public ResponseEntity<ApiResponse<Void>> addBookmark(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal UUID userId
    ) {
        bookmarkService.addBookmark(userId, articleId);
        return ResponseEntity.ok(ApiResponse.ok("북마크가 추가되었습니다.", null));
    }

    @Operation(summary = "북마크 삭제")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal UUID userId
    ) {
        bookmarkService.removeBookmark(userId, articleId);
        return ResponseEntity.ok(ApiResponse.ok("북마크가 삭제되었습니다.", null));
    }

    @Operation(summary = "내 북마크 목록")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleResponse>>> getMyBookmarks(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(bookmarkService.getMyBookmarks(userId, size))
        );
    }

    @Operation(summary = "북마크 여부 확인")
    @GetMapping("/{articleId}/check")
    public ResponseEntity<ApiResponse<Boolean>> isBookmarked(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(bookmarkService.isBookmarked(userId, articleId))
        );
    }
}