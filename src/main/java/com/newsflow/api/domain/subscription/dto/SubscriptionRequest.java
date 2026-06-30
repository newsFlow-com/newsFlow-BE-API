package com.newsflow.api.domain.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SubscriptionRequest {

    @NotBlank
    @Pattern(regexp = "keyword|category", message = "subscription_type은 keyword 또는 category이어야 합니다.")
    private String subscriptionType;

    @NotBlank
    @Size(max = 100)
    private String value;
}
