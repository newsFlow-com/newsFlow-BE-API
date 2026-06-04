package com.newsflow.api.domain.notice.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.notice.dto.NoticeCreateRequest;
import com.newsflow.api.domain.notice.dto.NoticeResponse;
import com.newsflow.api.domain.notice.dto.NoticeUpdateRequest;
import com.newsflow.api.domain.notice.repository.NoticeRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Notice;
import com.newsflow.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    // ── 사용자 게이트 ─────────────────────────────────────────────

    /**
     * 사용자 화면 공지사항 목록.
     * 고정 공지 우선, 노출 기간 유효한 것만 반환.
     */
    public List<NoticeResponse> getNoticesForUser(int size) {
        return noticeRepository
                .findActiveForUser(LocalDateTime.now(), PageRequest.of(0, size))
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    /**
     * 메인 화면 상단 고정 공지사항.
     */
    public List<NoticeResponse> getPinnedNotices() {
        return noticeRepository
                .findPinnedForUser(LocalDateTime.now())
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    /**
     * 공지사항 상세 조회.
     */
    public NoticeResponse getNotice(UUID noticeId) {
        Notice notice = findActiveNotice(noticeId);
        return NoticeResponse.from(notice);
    }

    // ── 관리자 게이트 ─────────────────────────────────────────────

    public List<NoticeResponse> getNoticesForAdmin(int size) {
        return noticeRepository
                .findActiveForAdmin(LocalDateTime.now(), PageRequest.of(0, size))
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request, UUID adminId) {
        User author = userRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .isPinned(request.isPinned())
                .targetGate(request.getTargetGate())
                .publishedAt(request.getPublishedAt())
                .expiredAt(request.getExpiredAt())
                .build();

        return NoticeResponse.from(noticeRepository.save(notice));
    }

    @Transactional
    public NoticeResponse updateNotice(UUID noticeId, NoticeUpdateRequest request) {
        Notice notice = findActiveNotice(noticeId);
        notice.update(
                request.getTitle(),
                request.getContent(),
                request.getIsPinned(),
                request.getIsActive(),
                request.getTargetGate(),
                request.getPublishedAt(),
                request.getExpiredAt()
        );
        return NoticeResponse.from(notice);
    }

    @Transactional
    public void deleteNotice(UUID noticeId) {
        Notice notice = findActiveNotice(noticeId);
        notice.deactivate();
    }

    // ── 내부 유틸 ─────────────────────────────────────────────────

    private Notice findActiveNotice(UUID noticeId) {
        return noticeRepository.findById(noticeId)
                .filter(n -> n.isActive())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}