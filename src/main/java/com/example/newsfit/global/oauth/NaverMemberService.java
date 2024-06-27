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
public class NaverMemberService {

    private final MemberService memberService;
    private final SecurityService securityService;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    public Pair<TokenResponse, Boolean> naverLogin(String accessToken) throws JsonProcessingException {

        MemberDto naverUserInfo = getNaverUserInfo(accessToken);

        Pair<Member, Boolean> naverMember = memberService.registerMemberIfNeed(naverUserInfo);

        Authentication authentication = securityService.forceLogin(naverMember.getLeft());

        return Pair.of(securityService.usersAuthorizationInput(authentication), naverMember.getRight());
    }


    // 카카오 엑세스 토큰 발급
    public String getAccessToken(String code, String state) throws JsonProcessingException {
        String reqUrl = "https://nid.naver.com/oauth2.0/token";

        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        //http 바디(params)와 http 헤더(headers)를 가진 엔티티
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        //reqUrl로 Http 요청 , POST 방식
        ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST, naverTokenRequest, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode naverResponse = mapper.readTree(response.getBody());

        return naverResponse.get("access_token").asText();
    }

    private MemberDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverUserInfoRequest,
                String.class
        );

        return handleNaverResponse(response.getBody());
    }

    private MemberDto handleNaverResponse(String responseBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String nickname = jsonNode.get("response")
                .get("nickname").asText();

        String email = jsonNode.get("response").get("email").asText();

        String thumbnailImage = jsonNode.get("response").get("profile_image").asText();

        return MemberDto.of(email, nickname, thumbnailImage);
    }

}
