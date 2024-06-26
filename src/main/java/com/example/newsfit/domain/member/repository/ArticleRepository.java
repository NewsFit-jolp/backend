package com.example.newsfit.domain.member.repository;

import com.example.newsfit.domain.member.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
