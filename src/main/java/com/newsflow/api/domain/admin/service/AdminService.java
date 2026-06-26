package com.newsflow.api.domain.admin.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.admin.dto.*;
import com.newsflow.api.domain.admin.repository.AdminArticleReportRepository;
import com.newsflow.api.domain.admin.repository.AdminPipelineStatRepository;
import com.newsflow.api.domain.admin.repository.ContentQualityLogRepository;
import com.newsflow.api.domain.article.repository.ArticleRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.*;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final AdminArticleReportRepository reportRepository;
    private final AdminPipelineStatRepository pipelineStatRepository;
    private final ContentQualityLogRepository qualityLogRepository;

    // ── 기사 관리 ─────────────────────────────────────────────────

    @Transactional
    public void hideArticle(UUID articleId) {
        Article article = findArticle(articleId);
        article.hide();
    }

    @Transactional
    public void restoreArticle(UUID articleId) {
        Article article = findArticle(articleId);
        article.restore();
    }

    @Transactional
    public void deleteArticle(UUID articleId) {
        Article article = findArticle(articleId);
        article.delete();
    }

    // ── 사용자 관리 ───────────────────────────────────────────────

    public List<AdminUserResponse> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(AdminUserResponse::from)
                .toList();
    }

    @Transactional
    public void suspendUser(UUID userId) {
        User user = findUser(userId);
        user.suspend();
    }

    @Transactional
    public void activateUser(UUID userId) {
        User user = findUser(userId);
        user.activate();
    }

    // ── 신고 처리 ─────────────────────────────────────────────────

    public List<ArticleReportResponse> getPendingReports(int size) {
        return reportRepository.findByStatus("pending", PageRequest.of(0, size))
                .stream()
                .map(this::toReportResponse)
                .toList();
    }

    @Transactional
    public void resolveReport(UUID reportId, UUID adminId, String resolution) {
        ArticleReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        report.resolve(adminId, resolution);
    }

    @Transactional
    public void dismissReport(UUID reportId, UUID adminId, String resolution) {
        ArticleReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        report.dismiss(adminId, resolution);
    }

    // ── 대시보드 ──────────────────────────────────────────────────

    public DashboardSummary getDashboard() {
        long totalArticles = articleRepository.count();
        long totalUsers = userRepository.count();
        long pendingReports = reportRepository.countByStatus("pending");

        // 오늘 수집 기사 수
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayCollected = articleRepository.countByCollectedAtAfter(todayStart);

        // 최근 파이프라인 상태 조회
        List<PipelineStat> latestList = pipelineStatRepository.findLatest(PageRequest.of(0, 1));
        PipelineStat latest = latestList.isEmpty() ? null : latestList.get(0);

        // 2. 외부 탑레벨 클래스로 완전히 분리된 PipelineStatus 타입으로 수정 및 빌더 호출
        PipelineStatus pipelineStatus = null;
        if (latest != null) {
            pipelineStatus = PipelineStatus.builder()
                    .dagId(latest.getDagId())
                    .status(latest.getStatus())
                    .collectedCount(latest.getCollectedCount())
                    .errorCount(latest.getErrorCount())
                    .startedAt(latest.getStartedAt() != null
                            ? latest.getStartedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            : null)
                    .build();
        }

        return DashboardSummary.builder()
                .totalArticles(totalArticles)
                .totalUsers(totalUsers)
                .todayCollected(todayCollected)
                .pendingReports(pendingReports)
                .latestPipeline(pipelineStatus)
                .build();
    }

    // ── 콘텐츠 품질 관리 ──────────────────────────────────────────

    public List<ContentQualityLogResponse> getQualityLogs(
            String checkType, Boolean isCorrect, int page, int size) {
        Page<ContentQualityLog> result = qualityLogRepository
                .findByFilters(checkType, isCorrect, PageRequest.of(page, size));
        return result.stream().map(ContentQualityLogResponse::from).toList();
    }

    @Transactional
    public void reviewQualityLog(UUID logId, UUID adminId, ContentQualityReviewRequest request) {
        ContentQualityLog log = qualityLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        log.review(adminId, request.getIsCorrect(), request.getCorrection());
    }

    // ── 내부 유틸 ─────────────────────────────────────────────────

    private Article findArticle(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private ArticleReportResponse toReportResponse(ArticleReport r) {
        return ArticleReportResponse.builder()
                .id(r.getId())
                .articleId(r.getArticle().getId())
                .articleTitle(r.getArticle().getTitle())
                .reason(r.getReason())
                .detail(r.getDetail())
                .status(r.getStatus())
                .reporterEmail(r.getReporter() != null ? r.getReporter().getEmail() : null)
                .createdAt(r.getCreatedAt() != null
                        ? r.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .build();
    }
}