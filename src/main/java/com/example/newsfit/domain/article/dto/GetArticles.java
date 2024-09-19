package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import io.swagger.v3.oas.annotations.media.Schema;


public record GetArticles(
        @Schema(description = "기사 제목", example = "기사 제목") String title,
        @Schema(description = "기사 내용", example = "기사 내용") String content ,
        @Schema(description = "언론사", example = "Chosun") Press press,
        @Schema(description = "카테고리", example = "IT") Category category

) {
    public static GetArticles of (Article article){
        return new GetArticles(
                article.getTitle(),
                article.getContent(),
                article.getPress(),
                article.getCategory()
        );
    }
}

