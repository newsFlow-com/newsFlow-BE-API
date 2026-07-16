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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private static final int MAX_SIZE = 50;

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

    public IssueDetailResponse getIssue(UUID issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ISSUE_NOT_FOUND));

        List<Article> articles = articleRepository.findByIssueIdOrderByPublishedAtDesc(issueId);
        return IssueDetailResponse.from(issue, articles);
    }
}
