package com.example.newsfit.domain.member.dto;

import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.member.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetPreferredCategories(
        @Schema(description = "선호 카테고리", example = "IT, Technology, Sports") List<Category> preferredCategories

        ) {
    public static GetPreferredCategories of(Member member) {
        return new GetPreferredCategories(member.getPreferredCategories());
    }
}
