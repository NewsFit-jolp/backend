package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Comment;
import com.example.newsfit.domain.article.entity.Press;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record GetArticle(
        @Schema(description = "제목") String title,
        @Schema(description = "내용") String content,
        @Schema(description = "언론사") Press press,
        @Schema(description = "카테고리") Category category,
        @Schema(description = "댓글") List<GetComment> comment
) {
    private static List<GetComment> getComments(Article article) {
        List<GetComment> commentDtoList = new ArrayList<>();
        List<Comment> comments = article.getComments();

        for (Comment comment : comments) {
            commentDtoList.add(GetComment.of(comment));
        }

        return commentDtoList;
    }
    public static GetArticle of(Article article) {
        return new GetArticle(
                article.getTitle(),
                article.getContent(),
                article.getPress(),
                article.getCategory(),
                getComments(article)
        );
    }
}
