package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record GetComment(
        @Schema(description = "댓글 내용", example = "댓글입니다.") String content,
        @Schema(description = "등록 일자", example = "2024-08-31T12:00:00 000") LocalDateTime createdDate
        ) {
    public static GetComment of(Comment comment){
        return new GetComment(
                comment.getContent(),
                comment.getCreatedDate()
        );
    }
}
