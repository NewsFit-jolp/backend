package com.example.newsfit.domain.member.entity;

import com.example.newsfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long member_id;

    private String email;
    private String phone;
    private String profileImage;
    private String nickname;
    private Date birth;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Categories> preferredCategories;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String email, String phone, String profileImage,
                  String nickname, Date birth, Role role) {
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.birth = birth;
        this.role = role;
        preferredCategories = new ArrayList<>();
    }
}
