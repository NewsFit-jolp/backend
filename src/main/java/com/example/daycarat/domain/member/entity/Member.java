package com.example.daycarat.domain.member.entity;

import com.example.daycarat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.SpringApplication;

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
    private String username;
    private String phone;
    private String profileImage;
    private String nickname;
    private Date birth;

    @ColumnDefault("true")
    private Boolean isActivated;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Categories> preferredCategories;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String email, String username,
                  String phone, String profileImage,
                  String nickname, Date birth, Role role) {
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.birth = birth;
        this.role = role;
        preferredCategories = new ArrayList<>();
    }
}
