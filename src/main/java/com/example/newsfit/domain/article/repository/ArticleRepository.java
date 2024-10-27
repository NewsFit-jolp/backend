package com.example.newsfit.domain.article.repository;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {


    List<Article> findByPressInOrderByArticleIdDesc(List<Press> pressList, Pageable pageable);

    List<Article> findByCategoryAndPressInOrderByArticleIdDesc(Category category, List<Press> pressList, Pageable pageable);

    List<Article> findByArticleIdLessThanAndPressInOrderByArticleIdDesc(Long ArticleId, List<Press> pressList, Pageable pageable);

    List<Article> findByArticleIdLessThanAndCategoryAndPressInOrderByArticleIdDesc(Long ArticleId, Category category, List<Press> pressList, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Article a " +
            "WHERE (a.title LIKE %:keyword% OR CAST(a.category AS string) LIKE %:keyword%) " +
            "AND a.articleId < :articleId " +
            "ORDER BY a.articleId DESC")
    List<Article> findByTitleOrCategoryContaining(
            @Param("keyword") String keyword,
            @Param("articleId") Long articleId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT a FROM Article a " +
            "WHERE (a.title LIKE %:keyword% OR CAST(a.category AS string) LIKE %:keyword%) " +
            "ORDER BY a.articleId DESC")
    List<Article> findAllByTitleOrCategoryContaining(
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
