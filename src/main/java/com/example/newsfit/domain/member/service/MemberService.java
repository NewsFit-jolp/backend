package com.example.newsfit.domain.member.service;

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
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.newsfit.global.util.Utils.jsonObjectParser;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
        String email = (String) jsonObject.get("email");
        String phone = (String) jsonObject.get("phone");
        Gender gender = Gender.valueOf((String) jsonObject.get("gender"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date birth = formatter.parse((String) jsonObject.get("birth"));

        member.putMember(name, email, phone, birth, gender);

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

    public Pair<Member, Boolean> registerMemberIfNeed(MemberDto MemberInfo) {

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

            return Pair.of(member, true);

        }
        return Pair.of(member, false);
    }
}
