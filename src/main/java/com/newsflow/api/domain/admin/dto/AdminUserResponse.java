package com.newsflow.api.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newsflow.api.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserResponse {

    private UUID id;
    private String email;
    private String nickname;
    private String role;
    private String status;

    public static AdminUserResponse from(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}