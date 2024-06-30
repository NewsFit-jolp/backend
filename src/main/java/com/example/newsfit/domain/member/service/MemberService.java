package com.example.newsfit.domain.member.service;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.example.newsfit.domain.member.dto.GetMemberInfo;
import com.example.newsfit.domain.member.dto.MemberDto;
import com.example.newsfit.domain.member.entity.Gender;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.entity.Role;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityManager em;

    public MemberService(MemberRepository memberRepository, EntityManagerFactory emf) {
        this.memberRepository = memberRepository;
        this.em = emf.createEntityManager();
    }

    public GetMemberInfo getMemberInfo() {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetMemberInfo.of(member);
    }

    public GetMemberInfo putMemberInfo(String requestBody) throws  ParseException, java.text.ParseException {
        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Member putMember = em.find(Member.class, member.getMemberId());

        JSONParser parser = new JSONParser();
        Object parsedBody = parser.parse(requestBody);
        JSONObject jsonObject = (JSONObject) parsedBody;

        String name = (String) jsonObject.get("name");
        String email = (String) jsonObject.get("email");
        String phone = (String) jsonObject.get("phone");
        Gender gender = Gender.valueOf((String) jsonObject.get("gender"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date birth = formatter.parse((String) jsonObject.get("birth"));

        JSONArray preferredCategories = (JSONArray) jsonObject.get("preferredCategories");
        JSONArray preferredPress = (JSONArray) jsonObject.get("preferredPress");

        putMember.putMember(name, email, phone, birth, gender);
        putMember.putCategories(preferredCategories);
        putMember.putPress(preferredPress);
        transaction.commit();

        return GetMemberInfo.of(putMember);
    }

    @Transactional
    public Boolean deleteMember(){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return true;
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
