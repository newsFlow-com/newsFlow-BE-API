package com.newsflow.api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginRequest {

    @NotBlank(message = "네이버 인가 코드는 필수입니다.")
    private String code;

    @NotBlank(message = "state는 필수입니다.")
    private String state;
}
