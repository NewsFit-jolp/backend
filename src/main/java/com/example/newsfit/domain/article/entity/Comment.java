package com.example.newsfit.domain.article.entity;

import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long comment_id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "article")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "member")
    private Member member;

    @Builder
    public Comment(String content, Article article, Member member) {
        this.content = content;
        this.article = article;
        this.member = member;
    }
}
