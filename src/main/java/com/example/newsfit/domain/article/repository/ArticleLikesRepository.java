package com.example.newsfit.domain.article.repository;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.ArticleLike;
import com.example.newsfit.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikesRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByMemberAndArticle(Member member, Article article);

}
