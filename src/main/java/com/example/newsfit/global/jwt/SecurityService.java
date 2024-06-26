package com.example.newsfit.global.jwt;

import com.example.newsfit.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class SecurityService {

    private final TokenService tokenService;

    public Authentication forceLogin(Member member) {
        UserDetails userDetails = new MemberDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public TokenResponse usersAuthorizationInput(Authentication authentication) {
        MemberDetailsImpl memberDetailsImpl = ((MemberDetailsImpl) authentication.getPrincipal());
        String accessToken = tokenService.createAccessToken(memberDetailsImpl);
        String refreshToken = tokenService.createRefreshToken(memberDetailsImpl);

        return new TokenResponse(accessToken, refreshToken);
    }
}
