package com.newsflow.api.domain.subscription.repository;

import com.newsflow.api.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<Subscription> findByUserIdAndSubscriptionTypeAndValue(
            UUID userId, String subscriptionType, String value);

    boolean existsByUserIdAndSubscriptionTypeAndValue(
            UUID userId, String subscriptionType, String value);
}
