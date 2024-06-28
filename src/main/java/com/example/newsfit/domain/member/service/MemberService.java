package com.example.newsfit.domain.member.service;

import com.example.newsfit.domain.member.dto.GetMemberInfo;
import com.example.newsfit.domain.member.dto.MemberDto;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.entity.Role;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public GetMemberInfo getUserInfo() {
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetMemberInfo.of(member);
    }

    @Transactional
    public Boolean deleteUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        memberRepository.delete(member);

        return true;
    }

    public Pair<Member, Boolean> registerMemberIfNeed(MemberDto MemberInfo) {

        String memberEmail = MemberInfo.getEmail();
        String memberNickname = MemberInfo.getNickname();
        String memberProfileImage = MemberInfo.getProfileImage();

        Member member = memberRepository.findByEmail(memberEmail)
                .orElse(null);

        System.out.println(member);

        if (member == null) {

            member = Member.builder()
                    .email(memberEmail)
                    .nickname(memberNickname)
                    .profileImage(memberProfileImage)
                    .role(Role.USER)
                    .build();

            memberRepository.save(member);

            return Pair.of(member, true);

        }
        return Pair.of(member, false);
    }
}
