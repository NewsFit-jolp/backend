package com.example.newsfit.domain.member.service;

import com.example.newsfit.domain.article.dto.LangChainRequest;
import com.example.newsfit.domain.member.dto.GetMemberInfo;
import com.example.newsfit.domain.member.dto.GetPreferredCategories;
import com.example.newsfit.domain.member.dto.GetPreferredPress;
import com.example.newsfit.domain.member.dto.MemberDto;
import com.example.newsfit.domain.member.entity.Gender;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.entity.Role;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import com.example.newsfit.global.jwt.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.example.newsfit.global.util.Utils.jsonObjectParser;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    @Value("${recommender.endpoint}")
    private String recommenderEndpoint;

    public GetMemberInfo getMemberInfo() {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetMemberInfo.of(member);
    }

    @Transactional
    public GetMemberInfo putMemberInfo(String requestBody) throws ParseException, java.text.ParseException {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        JSONObject jsonObject = jsonObjectParser(requestBody);

        String name = (String) jsonObject.get("name");
        String phone = (String) jsonObject.get("phone");
        Gender gender = Gender.valueOf((String) jsonObject.get("gender"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date birth = formatter.parse((String) jsonObject.get("birth"));

        member.putMember(name, phone, birth, gender);

        return GetMemberInfo.of(member);
    }

    @Transactional
    public GetPreferredCategories putPreferredCategories(String requestBody) throws ParseException {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        JSONObject jsonObject = jsonObjectParser(requestBody);

        JSONArray preferredCategories = (JSONArray) jsonObject.get("preferredCategories");
        member.putCategories(preferredCategories);

        return GetPreferredCategories.of(member);
    }

    @Transactional
    public GetPreferredPress putPreferredPress(String requestBody) throws ParseException {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        JSONObject jsonObject = jsonObjectParser(requestBody);

        JSONArray preferredPress = (JSONArray) jsonObject.get("preferredPress");
        member.putPress(preferredPress);

        return GetPreferredPress.of(member);
    }

    @Transactional
    public Boolean deleteMember() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        member.deleteMember();

        return true;
    }

    public GetPreferredCategories getPreferredCategories() {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetPreferredCategories.of(member);
    }

    public GetPreferredPress getPreferredPress() {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetPreferredPress.of(member);
    }

    @Transactional
    public Boolean deleteUser() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        memberRepository.delete(member);

        return true;
    }

    public Pair<Member, Boolean> registerMemberIfNeed(MemberDto MemberInfo) throws JsonProcessingException {

        String memberId = MemberInfo.getMemberId();
        String memberEmail = MemberInfo.getEmail();
        String memberNickname = MemberInfo.getNickname();
        String memberProfileImage = MemberInfo.getProfileImage();

        Member member = memberRepository.findByMemberId(memberId)
                .orElse(null);

        if (member == null) {

            member = Member.builder()
                    .memberId(memberId)
                    .email(memberEmail)
                    .nickname(memberNickname)
                    .profileImage(memberProfileImage)
                    .role(Role.USER)
                    .build();

            memberRepository.save(member);

            if (!registerMemberForRecommender(member.getId())) throw new CustomException(ErrorCode.USER_ALREADY_ADDED);
            return Pair.of(member, true);
        }
        return Pair.of(member, false);
    }

    public String reissueToken() {
        return tokenService.reissueAccessToken(SecurityContextHolder.getContext().getAuthentication());
    }

    public String getAdminToken() {
        Member admin = memberRepository.findByMemberId("admin").orElse(null);

        if (admin == null) {
            admin = Member.builder()
                    .memberId("admin")
                    .email("jolup.newsfit@admin.com")
                    .nickname("관리자")
                    .role(Role.ADMIN)
                    .build();
            memberRepository.save(admin);
        }

        return tokenService.createAdminAccessToken();
    }

    private Boolean registerMemberForRecommender(Long memberId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = String.format("{ \"user_id\": %d }", memberId);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                recommenderEndpoint + "/new-user",
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            return responseMap.get("status").equals("new user added");
        } else {
            throw new RuntimeException("Failed to send POST request: " + response.getStatusCode());
        }
    }
}
