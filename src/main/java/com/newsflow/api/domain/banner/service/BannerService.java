package com.newsflow.api.domain.banner.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.banner.dto.BannerCreateRequest;
import com.newsflow.api.domain.banner.dto.BannerResponse;
import com.newsflow.api.domain.banner.dto.BannerUpdateRequest;
import com.newsflow.api.domain.banner.repository.BannerRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Banner;
import com.newsflow.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;
    private final UserRepository userRepository;

    public List<BannerResponse> getActiveBanners(String position) {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = (position != null && !position.isBlank())
                ? bannerRepository.findActiveByPosition(position, now)
                : bannerRepository.findAllActive(now);
        return banners.stream().map(BannerResponse::from).toList();
    }

    @Transactional
    public BannerResponse createBanner(BannerCreateRequest request, UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Banner banner = Banner.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .position(request.getPosition())
                .displayOrder(request.getDisplayOrder())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .createdBy(admin)
                .build();

        return BannerResponse.from(bannerRepository.save(banner));
    }

    @Transactional
    public BannerResponse updateBanner(UUID bannerId, BannerUpdateRequest request) {
        Banner banner = findBanner(bannerId);
        banner.update(
                request.getTitle(),
                request.getImageUrl(),
                request.getLinkUrl(),
                request.getPosition(),
                request.getDisplayOrder(),
                request.getIsActive(),
                request.getStartAt(),
                request.getEndAt()
        );
        return BannerResponse.from(banner);
    }

    @Transactional
    public void deleteBanner(UUID bannerId) {
        Banner banner = findBanner(bannerId);
        banner.deactivate();
    }

    private Banner findBanner(UUID bannerId) {
        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
