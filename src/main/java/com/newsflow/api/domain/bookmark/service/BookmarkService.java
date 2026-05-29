package com.newsflow.api.domain.bookmark.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.article.dto.ArticleResponse;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.bookmark.repository.BookmarkRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Article;
import com.newsflow.api.entity.Bookmark;
import com.newsflow.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 💡 기본 설정을 readOnly = true로 두어 조회 메서드 성능 향상
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional // 💡 쓰기 작업에만 수동 선언
    public void addBookmark(UUID userId, UUID articleId) {
        if (bookmarkRepository.existsByUserIdAndArticleId(userId, articleId)) {
            throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
                .filter(Article::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        bookmarkRepository.save(Bookmark.builder().user(user).article(article).build());
    }

    @Transactional // 💡 쓰기 작업에만 수동 선언
    public void removeBookmark(UUID userId, UUID articleId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndArticleId(userId, articleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));
        bookmarkRepository.delete(bookmark);
    }

    public List<ArticleResponse> getMyBookmarks(UUID userId, int size) {
        // 💡 최적화된 다중 훼치 조인 메서드로 교체하여 쿼리 1발로 성능 최적화 보장
        return bookmarkRepository
                .findByUserIdWithDetails(userId, PageRequest.of(0, Math.min(size, 50)))
                .stream()
                .map(b -> ArticleResponse.from(b.getArticle()))
                .toList();
    }

    public boolean isBookmarked(UUID userId, UUID articleId) {
        return bookmarkRepository.existsByUserIdAndArticleId(userId, articleId);
    }
}