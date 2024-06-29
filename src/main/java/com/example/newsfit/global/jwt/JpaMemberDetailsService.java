package com.example.newsfit.global.jwt;

import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.example.newsfit.global.error.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class JpaMemberDetailsService implements UserDetailsService{

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) {
        Member member = memberRepository.findByMemberId(Long.parseLong(memberId)).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        return new MemberDetailsImpl(member);
    }
}