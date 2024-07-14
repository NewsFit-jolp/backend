package com.example.newsfit.global.error.exception;

import lombok.Getter;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@Getter
public class CustomAccessDeniedException extends AccessDeniedException {

    private final ErrorCode errorCode;

    public CustomAccessDeniedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomAccessDeniedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
