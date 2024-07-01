package com.example.newsfit.global.jwt;

import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomAccessDeniedException;
import com.example.newsfit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.example.newsfit.global.error.exception.ErrorCode.USER_NOT_FOUND;
import static com.example.newsfit.global.error.exception.ErrorCode.WITHDRAWAL_USER;

@Service
@RequiredArgsConstructor
public class JpaMemberDetailsService implements UserDetailsService{

    private final MemberRepository memberRepository;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        if (member.getIsDeleted()) throw new CustomAccessDeniedException(WITHDRAWAL_USER);
        return new MemberDetailsImpl(member);
    }
}