package com.example.newsfit.global.oauth;

import com.example.newsfit.domain.member.dto.KakaoMemberDto;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.entity.Role;
import com.example.newsfit.domain.member.repository.MemberRepository;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoMemberService {

    private final MemberRepository memberRepository;
    private final SecurityService securityService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public Pair<TokenResponse, Boolean> kakaoLogin(String accessToken) throws JsonProcessingException {

        KakaoMemberDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        Pair<Member, Boolean> kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo);

        Authentication authentication = securityService.forceLogin(kakaoUser.getLeft());

        return Pair.of(securityService.usersAuthorizationInput(authentication), kakaoUser.getRight());

    }


    // 카카오 엑세스 토큰 발급
    public String getAccessToken(String code) throws JsonProcessingException {
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        //http 바디(params)와 http 헤더(headers)를 가진 엔티티
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        //reqUrl로 Http 요청 , POST 방식
        ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST, kakaoTokenRequest, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode kakaoResponse = mapper.readTree(response.getBody());
        String accessToken = kakaoResponse.get("access_token").asText();

        return accessToken;
    }

    private KakaoMemberDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        return handleKakaoResponse(response.getBody());
    }

    private KakaoMemberDto handleKakaoResponse(String responseBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        String email = jsonNode.get("kakao_account").get("email").asText();

        String thumbnailImage = jsonNode.get("kakao_account").get("profile").get("thumbnail_image_url").asText();

        return KakaoMemberDto.of(email, nickname, thumbnailImage);
    }

    private Pair<Member, Boolean> registerKakaoUserIfNeed(KakaoMemberDto kakaoUserInfo) {

        String kakaoEmail = kakaoUserInfo.getEmail();
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail)
                .orElse(null);

        if (kakaoMember == null) {

            kakaoMember = Member.builder()
                    .email(kakaoEmail)
                    .nickname(kakaoUserInfo.getNickname())
                    .profileImage(kakaoUserInfo.getProfileImage())
                    .role(Role.USER)
                    .build();

            memberRepository.save(kakaoMember);

            return Pair.of(kakaoMember, true);

        }
        return Pair.of(kakaoMember, false);
    }
}
