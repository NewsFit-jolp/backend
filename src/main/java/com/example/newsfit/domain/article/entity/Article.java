package com.example.newsfit.domain.article.entity;

import com.example.newsfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    private String title;
    private String content;
    
    @ElementCollection
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private Press press;
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @ColumnDefault("0")
    private int likeCount;

    @Builder
    public Article(String title, String content, Press press,
                   Category category, List<String> images) {
        this.title = title;
        this.content = content;
        this.press = press;
        this.category = category;
        this.images = images;
    }

    public void addLikeCount() {
        likeCount++;
    }

    public void subLikeCount() {
        likeCount--;
    }
}
