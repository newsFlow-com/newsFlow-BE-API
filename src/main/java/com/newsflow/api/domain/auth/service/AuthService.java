package com.newsflow.api.domain.auth.service;

import com.newsflow.api.common.exception.BusinessException;
import com.newsflow.api.common.exception.ErrorCode;
import com.newsflow.api.common.util.JwtUtil;
import com.newsflow.api.domain.auth.dto.*;
import com.newsflow.api.domain.auth.repository.RefreshTokenRepository;
import com.newsflow.api.domain.user.repository.UserRepository;
import com.newsflow.api.entity.RefreshToken;
import com.newsflow.api.entity.SocialAccount;
import com.newsflow.api.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("user")
                .build();
        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getId(), user.getEmail());
        } catch (Exception e) {
            log.warn("회원가입 이메일 인증 발송 실패: userId={}", user.getId());
        }

        return issueTokens(user, "user");
    }

    public TokenResponse login(LoginRequest request) {
        String gate = request.getGate() != null ? request.getGate() : "user";

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED);
        }

        if ("admin".equals(gate) && !user.isAdmin()) {
            throw new BusinessException(ErrorCode.INVALID_GATE);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.updateLastLogin();
        return issueTokens(user, gate);
    }

    public TokenResponse kakaoLogin(String code) {
        Map<String, Object> tokenData = getKakaoToken(code);
        String kakaoAccessToken = (String) tokenData.get("access_token");

        Map<String, Object> userInfo = getKakaoUserInfo(kakaoAccessToken);
        String kakaoId = String.valueOf(userInfo.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.getOrDefault("email", kakaoId + "@kakao.local");
        String nickname = (String) profile.getOrDefault("nickname", "카카오유저");
        String profileImage = (String) profile.getOrDefault("profile_image_url", null);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .profileImageUrl(profileImage)
                    .role("user")
                    .build();

            userRepository.save(newUser);

            SocialAccount social = SocialAccount.builder()
                    .user(newUser)
                    .provider("kakao")
                    .providerUid(kakaoId)
                    .accessToken(kakaoAccessToken)
                    .build();

            newUser.getSocialAccounts().add(social);

            return newUser;
        });

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED);
        }

        user.updateLastLogin();
        return issueTokens(user, "user");
    }

    public TokenResponse naverLogin(String code, String state) {
        Map<String, Object> tokenData = getNaverToken(code, state);
        String naverAccessToken = (String) tokenData.get("access_token");

        Map<String, Object> userInfo = getNaverUserInfo(naverAccessToken);
        Map<String, Object> response = (Map<String, Object>) userInfo.get("response");

        String naverId = (String) response.get("id");
        String email = (String) response.getOrDefault("email", naverId + "@naver.local");
        String nickname = (String) response.getOrDefault("nickname", "네이버유저");
        String profileImage = (String) response.getOrDefault("profile_image", null);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .profileImageUrl(profileImage)
                    .role("user")
                    .build();

            userRepository.save(newUser);

            SocialAccount social = SocialAccount.builder()
                    .user(newUser)
                    .provider("naver")
                    .providerUid(naverId)
                    .accessToken(naverAccessToken)
                    .build();

            newUser.getSocialAccounts().add(social);

            return newUser;
        });

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED);
        }

        user.updateLastLogin();
        return issueTokens(user, "user");
    }

    public TokenResponse refresh(RefreshRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        User user = refreshToken.getUser();
        String gate = refreshToken.getGate();

        refreshToken.revoke();
        return issueTokens(user, gate);
    }

    public void sendVerificationEmail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        emailService.sendVerificationEmail(userId, user.getEmail());
    }

    public void verifyEmail(String token) {
        UUID userId = emailService.verifyToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.verifyEmail();
    }

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public void logout(String refreshTokenStr, String gate) {
        String tokenHash = hashToken(refreshTokenStr);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(RefreshToken::revoke);
    }

    public void requestPasswordReset(String email) {
        // 이메일 존재 여부를 외부에 노출하지 않기 위해 항상 성공 응답
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isActive()) {
                emailService.sendPasswordResetEmail(user.getId(), user.getEmail());
            }
        });
    }

    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        UUID userId = emailService.verifyResetToken(request.getToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private TokenResponse issueTokens(User user, String gate) {
        refreshTokenRepository.revokeAllByUserIdAndGate(user.getId(), gate);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole(), gate);
        String rawRefreshToken = jwtUtil.generateRefreshToken(user.getId(), gate);
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .gate(gate)
                .expiresAt(LocalDateTime.now().plusDays("admin".equals(gate) ? 7 : 30))
                .build();
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(
                accessToken,
                rawRefreshToken,
                gate,
                new TokenResponse.UserInfo(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getRole()
                )
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("토큰 해싱 실패", e);
        }
    }

    private Map<String, Object> getKakaoToken(String code) {
        // 카카오 규격: x-www-form-urlencoded 데이터 포맷 명시 전송 고정
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", System.getenv("KAKAO_REST_API_KEY"));
        formData.add("redirect_uri", System.getenv().getOrDefault("KAKAO_REDIRECT_URI", "http://localhost:3000/auth/kakao/callback"));
        formData.add("code", code);

        return WebClient.create("https://kauth.kakao.com")
                .post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        return WebClient.create("https://kapi.kakao.com")
                .get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private Map<String, Object> getNaverToken(String code, String state) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", System.getenv("NAVER_CLIENT_ID"));
        formData.add("client_secret", System.getenv("NAVER_CLIENT_SECRET"));
        formData.add("code", code);
        formData.add("state", state);

        return WebClient.create("https://nid.naver.com")
                .post()
                .uri("/oauth2.0/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private Map<String, Object> getNaverUserInfo(String accessToken) {
        return WebClient.create("https://openapi.naver.com")
                .get()
                .uri("/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}