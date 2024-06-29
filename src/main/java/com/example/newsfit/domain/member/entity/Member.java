package com.example.newsfit.domain.member.entity;

import com.example.newsfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;
    private String phone;
    private String profileImage;
    private Date birth;
    private Gender gender;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Categories> preferredCategories;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Press> preferredPress;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(Long memberId, String email, String phone, String profileImage,
                  String nickname, Date birth, Role role, Gender gender) {
        this.memberId = memberId;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.birth = birth;
        this.role = role;
        this.gender = gender;
        preferredCategories = new ArrayList<>();
    }

    public Member putMember(String nickname, String email, String phone,
                            Date birth, Gender gender) {
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
        return this;
    }

    public Member putCategories(JSONArray categories) {
        preferredCategories = new ArrayList<>();
        for (Object category : categories) {
            preferredCategories.add(Categories.valueOf((String) category));
        }
        return this;
    }

    public Member putPress(JSONArray presses) {
        preferredPress = new ArrayList<>();
        for (Object press : presses) {
            preferredPress.add(Press.valueOf((String) press));
        }
        return this;
    }
}
