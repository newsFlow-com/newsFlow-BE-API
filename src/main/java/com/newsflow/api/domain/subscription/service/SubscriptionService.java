package com.newsflow.api.domain.subscription.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.domain.subscription.dto.SubscriptionRequest;
import com.newsflow.api.domain.subscription.dto.SubscriptionResponse;
import com.newsflow.api.domain.subscription.repository.SubscriptionRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.Subscription;
import com.newsflow.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public List<SubscriptionResponse> getMySubscriptions(UUID userId) {
        return subscriptionRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(SubscriptionResponse::from)
                .toList();
    }

    @Transactional
    public SubscriptionResponse subscribe(UUID userId, SubscriptionRequest request) {
        if (subscriptionRepository.existsByUserIdAndSubscriptionTypeAndValue(
                userId, request.getSubscriptionType(), request.getValue())) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Subscription sub = Subscription.builder()
                .user(user)
                .subscriptionType(request.getSubscriptionType())
                .value(request.getValue())
                .build();

        return SubscriptionResponse.from(subscriptionRepository.save(sub));
    }

    @Transactional
    public void unsubscribe(UUID userId, UUID subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!sub.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        sub.deactivate();
    }
}
