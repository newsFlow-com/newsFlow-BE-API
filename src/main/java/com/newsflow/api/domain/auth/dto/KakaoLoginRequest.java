package com.newsflow.api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoLoginRequest {

    @NotBlank(message = "카카오 인가 코드는 필수입니다.")
    private String code;
}