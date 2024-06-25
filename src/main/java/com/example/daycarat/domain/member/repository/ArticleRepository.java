package com.example.daycarat.domain.member.repository;

import com.example.daycarat.domain.member.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
