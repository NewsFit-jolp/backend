package com.example.daycarat.domain.member.service;

import com.example.daycarat.domain.member.dto.GetMemberInfo;
import com.example.daycarat.domain.member.entity.Member;
import com.example.daycarat.domain.member.repository.MemberRepository;
import com.example.daycarat.global.error.exception.CustomException;
import com.example.daycarat.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
}
