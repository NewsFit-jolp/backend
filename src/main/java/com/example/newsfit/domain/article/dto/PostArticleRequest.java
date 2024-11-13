package com.example.newsfit.domain.article.dto;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record PostArticleRequest(
        @Schema(description = "제목", example = "뉴스 제목") String title,
        @Schema(description = "언론사", example = "중앙일보") Press press,
        @Schema(description = "카테고리", example = "정치") String category,
        @Schema(description = "이미지") List<String> images,
        @Schema(description = "원본 링크", example = "https://www.example.com") String articleSource,
        @Schema(description = "출간일", example = "2021-08-01T00:00:00") String publishDate
) {
    public Article toArticle() {
        return Article.builder()
                .title(title)
                .press(press)
                .category(Category.fromDisplayName(category))
                .images(images)
                .articleSource(articleSource)
                .publishDate(LocalDateTime.parse(publishDate))
                .build();

    }
}
