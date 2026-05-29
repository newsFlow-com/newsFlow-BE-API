package com.newsflow.api.domain.auth.dto;

import lombok.Getter;

@Getter
public class TokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String gate;
    private final UserInfo user;

    public TokenResponse(String accessToken, String refreshToken, String gate, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.gate = gate;
        this.user = user;
    }

    @Getter
    public static class UserInfo {
        private final String id;
        private final String email;
        private final String nickname;
        private final String role;

        public UserInfo(String id, String email, String nickname, String role) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.role = role;
        }
    }
}