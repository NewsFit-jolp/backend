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


    List<Article> findAllByOrderByArticleIdDesc(Pageable pageable);

    List<Article> findByCategoryOrderByArticleIdDesc(Category category, Pageable pageable);

    List<Article> findByArticleIdLessThanOrderByArticleIdDesc(Long ArticleId, Pageable pageable);

    List<Article> findByArticleIdLessThanAndCategoryOrderByArticleIdDesc(Long ArticleId, Category category, Pageable pageable);

    Page<Article> findAll(Pageable pageable);
}
