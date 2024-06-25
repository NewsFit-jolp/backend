package com.example.daycarat.domain.member.entity;

import com.example.daycarat.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long comment_id;

    private Long article_id;
    private String content;
    private Long member_id;
    private Long parent_comment_id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Comment(Long article_id, String content, Long member_id, Long parent_comment_id) {
        this.article_id = article_id;
        this.content = content;
        this.member_id = member_id;
        this.parent_comment_id = parent_comment_id;
    }
}
