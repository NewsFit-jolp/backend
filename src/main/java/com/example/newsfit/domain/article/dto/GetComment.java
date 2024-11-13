package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public record GetComment(
        @Schema(description = "댓글 아이디", example = "1") Long commentId,
        @Schema(description = "댓글 내용", example = "댓글입니다.") String content,
        @Schema(description = "작성자") String nickName,
        @Schema(description = "좋아요 수") Integer likeCount,
        @Schema(description = "등록 일자", example = "2024-08-31T12:00:00 000") LocalDateTime createdDate,
        @Schema(description = "내가 작성한 댓글인지 여부", example = "true") Boolean isMyComment) {

    public static GetComment of(Comment comment) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new GetComment(
                comment.getComment_id(),
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getLikeCount(),
                comment.getCreatedDate().toLocalDateTime(),
                comment.getMember().getOAuthId().equals(memberId)
        );
    }
}
