package com.newsflow.api.domain.issue.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.issue.dto.IssueDetailResponse;
import com.newsflow.api.domain.issue.dto.IssueResponse;
import com.newsflow.api.domain.issue.repository.IssueRepository;
import com.newsflow.api.entity.Article;
import com.newsflow.api.entity.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private static final int MAX_SIZE = 50;
    private static final int MIN_SOURCE_COUNT_FOR_BREAKING = 2;
    private static final int BREAKING_CANDIDATE_POOL = 100;
    /** 분모가 0에 가까워 속도 점수가 무한대로 튀는 것을 막기 위한 최소 경과시간(시간 단위) */
    private static final double MIN_ELAPSED_HOURS = 0.5;

    private final IssueRepository issueRepository;
    private final ArticleRepository articleRepository;

    public List<IssueResponse> getIssues(String categorySlug, String sort, int size) {
        size = Math.min(size, MAX_SIZE);
        var pageable = PageRequest.of(0, size);

        boolean byCount = "count".equalsIgnoreCase(sort);
        boolean hasCategory = categorySlug != null && !categorySlug.isBlank();

        List<Issue> issues;
        if (hasCategory) {
            issues = byCount
                    ? issueRepository.findActiveByCategoryOrderByArticleCount(categorySlug, pageable)
                    : issueRepository.findActiveByCategoryOrderByLatest(categorySlug, pageable);
        } else {
            issues = byCount
                    ? issueRepository.findActiveOrderByArticleCount(pageable)
                    : issueRepository.findActiveOrderByLatest(pageable);
        }

        return issues.stream().map(IssueResponse::from).toList();
    }

    /**
     * 속보 — 최근 hours시간 내 갱신되고 매체 2곳 이상이 다룬 이슈를
     * 속도 점수(source_count / 최초보도 이후 경과시간) 내림차순으로 반환한다.
     */
    public List<IssueResponse> getBreakingIssues(int hours, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_SIZE);
        LocalDateTime since = LocalDateTime.now().minusHours(Math.max(hours, 1));

        List<Issue> candidates = issueRepository.findBreakingCandidates(
                since, MIN_SOURCE_COUNT_FOR_BREAKING, PageRequest.of(0, BREAKING_CANDIDATE_POOL));

        return candidates.stream()
                .map(issue -> {
                    double elapsedHours = issue.getFirstPublishedAt() != null
                            ? Duration.between(issue.getFirstPublishedAt(), LocalDateTime.now()).toMinutes() / 60.0
                            : MIN_ELAPSED_HOURS;
                    double score = issue.getSourceCount() / Math.max(elapsedHours, MIN_ELAPSED_HOURS);
                    return IssueResponse.from(issue).toBuilder().breakingScore(score).build();
                })
                .sorted(Comparator.comparingDouble(IssueResponse::getBreakingScore).reversed())
                .limit(safeLimit)
                .toList();
    }

    public IssueDetailResponse getIssue(UUID issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ISSUE_NOT_FOUND));

        List<Article> articles = articleRepository.findByIssueIdOrderByPublishedAtDesc(issueId);
        return IssueDetailResponse.from(issue, articles);
    }
}
