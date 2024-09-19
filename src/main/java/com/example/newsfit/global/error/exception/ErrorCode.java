package com.example.newsfit.global.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    AUTHENTICATION_REQUIRED(401, "C001", "인증이 필요합니다."),
    ACCESS_DENIED(403, "C002", "권한이 없는 사용자입니다."),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 에러입니다."),
    USER_NOT_FOUND(404, "C004", "존재하지 않는 사용자입니다."),
    INVALID_REQUEST_ERROR(400, "C005", "잘못된 요청입니다."),
    WITHDRAWAL_USER(401, "C006", "탈퇴한 사용자입니다."),
    ARTICLE_NOT_FOUND(404, "C007", "존재하지 않는 게시글입니다."),
    COMMENT_NOT_FOUND(404, "C008", "존재하지 않는 댓글입니다."),
    COMMENT_DELETE_FORBIDDEN(403, "C009", "댓글 삭제 권한이 없는 사용자입니다."),
    DUPLICATED_ARTICLE_LIKE(400, "C010", "이미 좋아요를 누른 게시글입니다.");


    private final int status;
    private final String code;
    private final String message;
}
