package com.example.newsfit.domain.article.repository;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByCategoryAndPress(Category category, Press press, Pageable pageable);

    List<Article> findByCategory(Category category, Pageable pageable);

    List<Article> findByPress(Press press, Pageable pageable);

    Page<Article> findAll(Pageable pageable);
}
