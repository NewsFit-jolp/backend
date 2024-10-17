package com.example.newsfit.domain.article.entity;

import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.global.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @JsonIgnore
    private Article article;

    @ManyToOne
    @JoinColumn(name = "member")
    @JsonIgnore
    private Member member;

    @ColumnDefault("0")
    private int likeCount;

    @Builder
    public Comment(String content, Article article, Member member) {
        this.content = content;
        this.article = article;
        this.member = member;
    }

    public Boolean deleteComment() {
        this.isDeleted = true;
        return true;
    }


    public void addLikeCount(){
        likeCount++;
    }

    public void subLikeCount(){
        likeCount--;
    }
}
