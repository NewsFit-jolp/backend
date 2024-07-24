package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import io.swagger.v3.oas.annotations.media.Schema;


public record GetArticle(
        @Schema(description = "기사 제목", example = "기사 제목") String title,
        @Schema(description = "기사 내용", example = "기사 내용") String content ,
        @Schema(description = "언론사", example = "Chosun") Press press,
        @Schema(description = "카테고리", example = "IT") Category category

) {
    public static GetArticle of (Article article){
        return new GetArticle(
                article.getTitle(),
                article.getContent(),
                article.getPress(),
                article.getCategory()
        );
    }
}

