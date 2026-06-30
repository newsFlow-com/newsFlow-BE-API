package com.newsflow.api.domain.banner.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.domain.banner.dto.BannerCreateRequest;
import com.newsflow.api.domain.banner.dto.BannerResponse;
import com.newsflow.api.domain.banner.dto.BannerUpdateRequest;
import com.newsflow.api.domain.banner.repository.BannerRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Banner;
import com.newsflow.api.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @InjectMocks BannerService bannerService;
    @Mock BannerRepository bannerRepository;
    @Mock UserRepository userRepository;

    @Test
    @DisplayName("position 지정 시 findActiveByPosition 쿼리가 호출된다")
    void getActiveBanners_withPosition_callsPositionFilter() {
        when(bannerRepository.findActiveByPosition(eq("main_top"), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<BannerResponse> result = bannerService.getActiveBanners("main_top");

        assertThat(result).isEmpty();
        verify(bannerRepository).findActiveByPosition(eq("main_top"), any(LocalDateTime.class));
        verify(bannerRepository, never()).findAllActive(any());
    }

    @Test
    @DisplayName("position 미지정 시 findAllActive 쿼리가 호출된다")
    void getActiveBanners_withoutPosition_callsAllActive() {
        when(bannerRepository.findAllActive(any(LocalDateTime.class))).thenReturn(List.of());

        bannerService.getActiveBanners(null);

        verify(bannerRepository).findAllActive(any(LocalDateTime.class));
        verify(bannerRepository, never()).findActiveByPosition(any(), any());
    }

    @Test
    @DisplayName("빈 문자열 position도 전체 조회로 처리된다")
    void getActiveBanners_withBlankPosition_callsAllActive() {
        when(bannerRepository.findAllActive(any(LocalDateTime.class))).thenReturn(List.of());

        bannerService.getActiveBanners("  ");

        verify(bannerRepository).findAllActive(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("배너 생성 시 Repository save가 호출된다")
    void createBanner_savesToRepository() {
        UUID adminId = UUID.randomUUID();
        User admin = mock(User.class);
        Banner savedBanner = mock(Banner.class);

        BannerCreateRequest request = new BannerCreateRequest();
        ReflectionTestUtils.setField(request, "title", "메인 배너");
        ReflectionTestUtils.setField(request, "imageUrl", "http://img.test/1.jpg");
        ReflectionTestUtils.setField(request, "position", "main_top");

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(bannerRepository.save(any(Banner.class))).thenReturn(savedBanner);

        bannerService.createBanner(request, adminId);

        verify(bannerRepository).save(any(Banner.class));
    }

    @Test
    @DisplayName("존재하지 않는 adminId로 배너 생성 시 BusinessException 발생")
    void createBanner_throwsWhenAdminNotFound() {
        UUID adminId = UUID.randomUUID();
        BannerCreateRequest request = new BannerCreateRequest();
        ReflectionTestUtils.setField(request, "title", "배너");
        ReflectionTestUtils.setField(request, "imageUrl", "http://img.test/1.jpg");
        ReflectionTestUtils.setField(request, "position", "sidebar");

        when(userRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.createBanner(request, adminId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("배너 수정 시 Banner 엔티티의 update()가 호출된다")
    void updateBanner_callsEntityUpdate() {
        UUID bannerId = UUID.randomUUID();
        Banner banner = mock(Banner.class);
        when(bannerRepository.findById(bannerId)).thenReturn(Optional.of(banner));

        BannerUpdateRequest request = new BannerUpdateRequest();
        ReflectionTestUtils.setField(request, "title", "수정된 배너");

        bannerService.updateBanner(bannerId, request);

        verify(banner).update(eq("수정된 배너"), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("배너 삭제 시 Banner 엔티티의 deactivate()가 호출된다")
    void deleteBanner_callsDeactivate() {
        UUID bannerId = UUID.randomUUID();
        Banner banner = mock(Banner.class);
        when(bannerRepository.findById(bannerId)).thenReturn(Optional.of(banner));

        bannerService.deleteBanner(bannerId);

        verify(banner).deactivate();
    }

    @Test
    @DisplayName("존재하지 않는 배너 삭제 시 BusinessException 발생")
    void deleteBanner_throwsWhenNotFound() {
        UUID bannerId = UUID.randomUUID();
        when(bannerRepository.findById(bannerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.deleteBanner(bannerId))
                .isInstanceOf(BusinessException.class);
    }
}
