package com.example.newsfit.domain.member.dto;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.member.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record GetPreferredCategories(
        @Schema(description = "선호 카테고리", example = "IT, Technology, Sports") List<String> preferredCategories
        ) {
    public static GetPreferredCategories of(Member member) {
        List<Category> preferredCategories = member.getPreferredCategories();
        List<String> displayName = new ArrayList<>();
        for (Category category : preferredCategories) {
            displayName.add(category.toString());
        }
        return new GetPreferredCategories(displayName);
    }
}
