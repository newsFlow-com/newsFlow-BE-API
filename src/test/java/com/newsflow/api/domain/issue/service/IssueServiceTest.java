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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

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
    @DisplayName("이슈 상세 조회 시 매체별 감성 분포가 집계된다")
    void getIssue_aggregatesSentimentDistribution() {
        UUID issueId = UUID.randomUUID();
        Issue issue = mock(Issue.class);
        when(issue.getId()).thenReturn(issueId);

        Article positive1 = mock(Article.class);
        when(positive1.getSentiment()).thenReturn("positive");
        when(positive1.getArticleCategories()).thenReturn(List.of());
        Article positive2 = mock(Article.class);
        when(positive2.getSentiment()).thenReturn("positive");
        when(positive2.getArticleCategories()).thenReturn(List.of());
        Article negative = mock(Article.class);
        when(negative.getSentiment()).thenReturn("negative");
        when(negative.getArticleCategories()).thenReturn(List.of());
        Article unanalyzed = mock(Article.class);
        when(unanalyzed.getSentiment()).thenReturn(null);
        when(unanalyzed.getArticleCategories()).thenReturn(List.of());

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(articleRepository.findByIssueIdOrderByPublishedAtDesc(issueId))
                .thenReturn(List.of(positive1, positive2, negative, unanalyzed));

        var result = issueService.getIssue(issueId);

        assertThat(result.getSentimentDistribution())
                .containsEntry("positive", 2L)
                .containsEntry("negative", 1L)
                .doesNotContainKey("neutral");
    }

    @Test
    @DisplayName("존재하지 않는 이슈 조회 시 BusinessException 발생")
    void getIssue_throwsWhenNotFound() {
        UUID issueId = UUID.randomUUID();
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> issueService.getIssue(issueId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("속보 후보가 없으면 빈 리스트를 반환한다")
    void getBreakingIssues_returnsEmptyWhenNoCandidates() {
        when(issueRepository.findBreakingCandidates(any(), eq(2), any(Pageable.class)))
                .thenReturn(List.of());

        var result = issueService.getBreakingIssues(3, 10);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("짧은 시간에 많은 매체가 붙은 이슈일수록 속도 점수가 높아 먼저 정렬된다")
    void getBreakingIssues_sortsByVelocityDescending() {
        Issue slow = mock(Issue.class); // 매체 2곳 / 4시간 경과 → 점수 0.5
        when(slow.getSourceCount()).thenReturn(2);
        when(slow.getFirstPublishedAt()).thenReturn(LocalDateTime.now().minusHours(4));

        Issue fast = mock(Issue.class); // 매체 4곳 / 1시간 경과 → 점수 4.0
        when(fast.getSourceCount()).thenReturn(4);
        when(fast.getFirstPublishedAt()).thenReturn(LocalDateTime.now().minusHours(1));

        when(issueRepository.findBreakingCandidates(any(), eq(2), any(Pageable.class)))
                .thenReturn(List.of(slow, fast));

        var result = issueService.getBreakingIssues(3, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSourceCount()).isEqualTo(4);
        assertThat(result.get(0).getBreakingScore()).isGreaterThan(result.get(1).getBreakingScore());
    }

    @Test
    @DisplayName("limit이 MAX_SIZE(50)를 넘으면 50개로 제한된다")
    void getBreakingIssues_capsLimitAtMax() {
        List<Issue> many = IntStream.range(0, 60)
                .mapToObj(i -> {
                    Issue issue = mock(Issue.class);
                    when(issue.getSourceCount()).thenReturn(2);
                    when(issue.getFirstPublishedAt()).thenReturn(LocalDateTime.now().minusHours(1));
                    return issue;
                })
                .toList();
        when(issueRepository.findBreakingCandidates(any(), eq(2), any(Pageable.class)))
                .thenReturn(many);

        var result = issueService.getBreakingIssues(3, 100);

        assertThat(result).hasSize(50);
    }

    @Test
    @DisplayName("firstPublishedAt이 없어도 예외 없이 처리된다")
    void getBreakingIssues_handlesNullFirstPublishedAt() {
        Issue issue = mock(Issue.class);
        when(issue.getSourceCount()).thenReturn(2);
        when(issue.getFirstPublishedAt()).thenReturn(null);

        when(issueRepository.findBreakingCandidates(any(), eq(2), any(Pageable.class)))
                .thenReturn(List.of(issue));

        var result = issueService.getBreakingIssues(3, 10);

        assertThat(result).hasSize(1);
    }
}
