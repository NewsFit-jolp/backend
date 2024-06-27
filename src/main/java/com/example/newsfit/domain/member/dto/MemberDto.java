package com.example.newsfit.domain.member.dto;

import com.example.newsfit.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberDto {
    private String email;
    private String nickname;
    private String profileImage;

    @Builder
    public MemberDto(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static MemberDto of(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }

    public static MemberDto of(String email, String nickname, String profileImage) {
        return MemberDto.builder()
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}