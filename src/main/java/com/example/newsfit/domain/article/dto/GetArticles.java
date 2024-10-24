package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


public record GetArticles(
        @Schema(description = "기사 제목", example = "기사 제목") String title,
        @Schema(description = "헤드라인", example = "헤드라인") String headLine,
        @Schema(description = "언론사", example = "Chosun") Press press,
        @Schema(description = "카테고리", example = "IT") Category category,
        @Schema(description = "썸네일", example = "www.example.com/images/1") String thumbnail,
        @Schema(description = "원본 기사의 게시일", example = "2024-10-25T10:00") LocalDateTime publishDate
) {
    public static GetArticles of(Article article) {
        return new GetArticles(
                article.getTitle(),
                article.getHeadLine(),
                article.getPress(),
                article.getCategory(),
                article.getImages().get(0),
                article.getPublishDate()
        );
    }
}

