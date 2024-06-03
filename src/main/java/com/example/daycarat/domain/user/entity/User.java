package com.example.daycarat.domain.user.entity;

import com.example.daycarat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity @Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;
    private String profileImage;
    private String password;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String email, String nickname, String profileImage, String password, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.password = password;
        this.role = role;
    }
}
