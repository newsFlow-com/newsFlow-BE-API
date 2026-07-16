package com.newsflow.api.domain.issue.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.issue.repository.IssueRepository;
import com.newsflow.api.entity.Article;
import com.newsflow.api.entity.Category;
import com.newsflow.api.entity.Issue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @InjectMocks IssueService issueService;
    @Mock IssueRepository issueRepository;
    @Mock ArticleRepository articleRepository;

    @Test
    @DisplayName("카테고리 없이 최신순 조회 시 findActiveOrderByLatest 호출")
    void getIssues_latestWithoutCategory() {
        Issue issue = mock(Issue.class);
        when(issueRepository.findActiveOrderByLatest(any(Pageable.class)))
                .thenReturn(List.of(issue));

        var result = issueService.getIssues(null, "latest", 20);

        assertThat(result).hasSize(1);
        verify(issueRepository).findActiveOrderByLatest(any(Pageable.class));
        verify(issueRepository, never()).findActiveByCategoryOrderByLatest(any(), any());
    }

    @Test
    @DisplayName("카테고리 지정 + count 정렬 시 findActiveByCategoryOrderByArticleCount 호출")
    void getIssues_countWithCategory() {
        Issue issue = mock(Issue.class);
        when(issueRepository.findActiveByCategoryOrderByArticleCount(eq("economy"), any(Pageable.class)))
                .thenReturn(List.of(issue));

        var result = issueService.getIssues("economy", "count", 20);

        assertThat(result).hasSize(1);
        verify(issueRepository).findActiveByCategoryOrderByArticleCount(eq("economy"), any(Pageable.class));
    }

    @Test
    @DisplayName("size가 MAX_SIZE(50)를 넘으면 50으로 제한된다")
    void getIssues_capsSizeAtMax() {
        when(issueRepository.findActiveOrderByLatest(any(Pageable.class))).thenReturn(List.of());

        issueService.getIssues(null, "latest", 100);

        verify(issueRepository).findActiveOrderByLatest(
                argThat(pageable -> pageable.getPageSize() == 50));
    }

    @Test
    @DisplayName("존재하는 이슈 조회 시 소속 기사 목록을 포함해 반환한다")
    void getIssue_returnsDetailWithArticles() {
        UUID issueId = UUID.randomUUID();
        Issue issue = mock(Issue.class);
        Category category = mock(Category.class);
        when(category.getSlug()).thenReturn("economy");
        when(category.getName()).thenReturn("경제");
        when(issue.getCategory()).thenReturn(category);
        when(issue.getId()).thenReturn(issueId);

        Article article = mock(Article.class);
        when(article.getArticleCategories()).thenReturn(List.of());
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(articleRepository.findByIssueIdOrderByPublishedAtDesc(issueId))
                .thenReturn(List.of(article));

        var result = issueService.getIssue(issueId);

        assertThat(result.getArticles()).hasSize(1);
        assertThat(result.getCategorySlug()).isEqualTo("economy");
    }

    @Test
    @DisplayName("존재하지 않는 이슈 조회 시 BusinessException 발생")
    void getIssue_throwsWhenNotFound() {
        UUID issueId = UUID.randomUUID();
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> issueService.getIssue(issueId))
                .isInstanceOf(BusinessException.class);
    }
}
