package com.example.daycarat.global.oauth;

import com.example.daycarat.domain.member.dto.KakaoMemberDto;
import com.example.daycarat.domain.member.entity.Member;
import com.example.daycarat.domain.member.entity.Role;
import com.example.daycarat.domain.member.repository.MemberRepository;
import com.example.daycarat.global.jwt.SecurityService;
import com.example.daycarat.global.jwt.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service @RequiredArgsConstructor
public class KakaoMemberService {

    private final MemberRepository memberRepository;
    private final SecurityService securityService;

//    @Value("${oauth.kakao.client-id}")
//    private String clientId;
//    @Value("${oauth.kakao.client-secret}")
//    private String clientSecret;

    public Pair<TokenResponse, Boolean> kakaoLogin(String accessToken) throws JsonProcessingException {

        KakaoMemberDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        Pair<Member, Boolean> kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo);

        Authentication authentication = securityService.forceLogin(kakaoUser.getLeft());

        return Pair.of(securityService.usersAuthorizationInput(authentication), kakaoUser.getRight());

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

    private Pair<Member, Boolean> registerKakaoUserIfNeed (KakaoMemberDto kakaoUserInfo) {

        String kakaoEmail = kakaoUserInfo.getEmail();
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail)
                .orElse(null);

        if (kakaoMember == null) {

            String password = UUID.randomUUID().toString();

            kakaoMember = Member.builder()
                    .email(kakaoEmail)
                    .nickname(kakaoUserInfo.getNickname())
                    .profileImage(kakaoUserInfo.getProfileImage())
                    .role(Role.USER)
                    .password(password)
                    .build();

            memberRepository.save(kakaoMember);

            return Pair.of(kakaoMember, true);

        }
        return Pair.of(kakaoMember, false);
    }
}
