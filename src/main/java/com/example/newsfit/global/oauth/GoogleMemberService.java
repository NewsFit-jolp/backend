package com.example.newsfit.global.oauth;

import com.example.newsfit.domain.member.dto.MemberDto;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.service.MemberService;
import com.example.newsfit.global.jwt.SecurityService;
import com.example.newsfit.global.jwt.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class GoogleMemberService {

    private final MemberService memberService;
    private final SecurityService securityService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    // 카카오 엑세스 토큰 발급
    public String getAccessToken(String code) throws JsonProcessingException {
        String reqUrl = "https://oauth2.googleapis.com/token";

        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        //http 바디(params)와 http 헤더(headers)를 가진 엔티티
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

        //reqUrl로 Http 요청 , POST 방식
        ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST, googleTokenRequest, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode googleResponse = mapper.readTree(response.getBody());

        return googleResponse.get("access_token").asText();
    }


    public Pair<TokenResponse, Boolean> googleLogin(String accessToken) throws JsonProcessingException {

        MemberDto googleUserInfo = getGoogleMemberInfo(accessToken);

        Pair<Member, Boolean> googleMember = memberService.registerMemberIfNeed(googleUserInfo);

        Authentication authentication = securityService.forceLogin(googleMember.getLeft());

        return Pair.of(securityService.usersAuthorizationInput(authentication), googleMember.getRight());
    }

    private MemberDto getGoogleMemberInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/userinfo/v2/me",
                HttpMethod.GET,
                googleMemberInfoRequest,
                String.class
        );

        return handleGoogleResponse(response.getBody());
    }

    private MemberDto handleGoogleResponse(String responseBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String nickname = jsonNode.get("name").asText();

        String email = jsonNode.get("email").asText();

        String thumbnailImage = jsonNode.get("picture").asText();

        return MemberDto.of(email, nickname, thumbnailImage);
    }

}
