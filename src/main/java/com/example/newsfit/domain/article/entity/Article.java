package com.example.newsfit.domain.article.entity;

import com.example.newsfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long articleId;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private Press press;
    @Enumerated(EnumType.STRING)
    private Category category;

    private String articleSource;
    private LocalDateTime publishDate;
    @Setter
    private String headLine;

    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @ColumnDefault("0")
    private int likeCount;

    @Builder
    public Article(String title, String content, Press press,
                   Category category, List<String> images,
                   LocalDateTime publishDate, String articleSource, String headLine) {
        this.title = title;
        this.content = content;
        this.press = press;
        this.category = category;
        this.images = images;
        this.publishDate = publishDate;
        this.articleSource = articleSource;
        this.headLine = headLine;
    }

    public void summaryArticle(String summary){
        this.content = summary;
    }

    public void addLikeCount() {
        likeCount++;
    }

    public void subLikeCount() {
        likeCount--;
    }
}
