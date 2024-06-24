package com.example.daycarat.domain.member.dto;

import com.example.daycarat.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoMemberDto {
    private String email;
    private String nickname;
    private String profileImage;

    @Builder
    public KakaoMemberDto(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static KakaoMemberDto of(Member member) {
        return KakaoMemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }

    public static KakaoMemberDto of(String email, String nickname, String profileImage) {
        return KakaoMemberDto.builder()
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}