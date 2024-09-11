package com.example.newsfit.domain.member.dto;

import com.example.newsfit.domain.member.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record GetMemberInfo(
        @Schema(description = "닉네임", example = "테스트") String nickname,
        @Schema(description = "이메일", example = "test@test.com") String email,
        @Schema(description = "프로필 사진", example = "https://d1j8r0kxyu9tj8.cloudfront.net/files/1617616479Z1X6X1X1/profile_pic.jpg") String profileImage,
        @Schema(description = "핸드폰 번호", example = "010-0000-0000") String phone,
        @Schema(description = "생일", example = "2024/06/30") Date birth,
        @Schema(description = "성별", example = "Male") Gender gender
        ) {
    public static GetMemberInfo of(Member member) {
        return new GetMemberInfo(
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage(),
                member.getPhone(),
                member.getBirth(),
                member.getGender()
        );
    }
}
