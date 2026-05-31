package com.newsflow.api.domain.user.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.category.repository.CategoryRepository;
import com.newsflow.api.domain.user.dto.UpdateProfileRequest;
import com.newsflow.api.domain.user.dto.UserCategoryRequest;
import com.newsflow.api.domain.user.dto.UserCategoryResponse;
import com.newsflow.api.domain.user.dto.UserProfileResponse;
import com.newsflow.api.domain.user.repository.UserCategoryRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Category;
import com.newsflow.api.entity.User;
import com.newsflow.api.entity.UserCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;

    // ── 내 프로필 조회 ────────────────────────────────────────────

    public UserProfileResponse getMyProfile(UUID userId) {
        User user = findActiveUser(userId);
        return UserProfileResponse.from(user);
    }

    // ── 프로필 수정 ───────────────────────────────────────────────

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findActiveUser(userId);
        user.updateProfile(request.getNickname(), request.getProfileImageUrl());
        return UserProfileResponse.from(user);
    }

    // ── 관심 카테고리 목록 조회 ───────────────────────────────────

    public List<UserCategoryResponse> getMyCategories(UUID userId) {
        return userCategoryRepository.findByUserId(userId).stream()
                .map(uc -> UserCategoryResponse.builder()
                        .categoryId(uc.getCategory().getId())
                        .slug(uc.getCategory().getSlug())
                        .name(uc.getCategory().getName())
                        .preferenceWeight(uc.getPreferenceWeight())
                        .build())
                .toList();
    }

    // ── 관심 카테고리 등록 ────────────────────────────────────────

    @Transactional
    public UserCategoryResponse addCategory(UUID userId, UserCategoryRequest request) {
        User user = findActiveUser(userId);
        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (userCategoryRepository.existsByUserIdAndCategoryId(userId, category.getId())) {
            // 이미 등록된 카테고리면 가중치만 업데이트
            UserCategory uc = userCategoryRepository
                    .findByUserIdAndCategoryId(userId, category.getId())
                    .orElseThrow();
            uc.updateWeight(request.getPreferenceWeight());
            return toResponse(uc);
        }

        UserCategory userCategory = UserCategory.builder()
                .user(user)
                .category(category)
                .preferenceWeight(request.getPreferenceWeight())
                .build();
        userCategoryRepository.save(userCategory);
        return toResponse(userCategory);
    }

    // ── 관심 카테고리 삭제 ────────────────────────────────────────

    @Transactional
    public void removeCategory(UUID userId, String categorySlug) {
        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        UserCategory uc = userCategoryRepository
                .findByUserIdAndCategoryId(userId, category.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        userCategoryRepository.delete(uc);
    }

    // ── 내부 유틸 ─────────────────────────────────────────────────

    private User findActiveUser(UUID userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private UserCategoryResponse toResponse(UserCategory uc) {
        return UserCategoryResponse.builder()
                .categoryId(uc.getCategory().getId())
                .slug(uc.getCategory().getSlug())
                .name(uc.getCategory().getName())
                .preferenceWeight(uc.getPreferenceWeight())
                .build();
    }
}