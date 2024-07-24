package com.example.newsfit.domain.member.dto;

import com.example.newsfit.domain.article.entity.Press;
import com.example.newsfit.domain.member.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetPreferredPress(
        @Schema(description = "선호 언론사", example = "Chosun, Joongang, Donga") List<Press> preferredPress

) {
    public static GetPreferredPress of(Member member) {
        return new GetPreferredPress(member.getPreferredPress());
    }
}
