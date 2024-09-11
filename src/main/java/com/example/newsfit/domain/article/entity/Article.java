package com.example.newsfit.domain.article.entity;

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
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long article_id;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private Press press;
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Article(String title, String content, Press press,
                   Category category) {
        this.title = title;
        this.content = content;
        this.press = press;
        this.category = category;
    }
}
