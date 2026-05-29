package com.newsflow.api.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException { // 💡 public 추가

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }
}