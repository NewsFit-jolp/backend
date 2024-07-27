package com.example.newsfit.domain.article.service;

import com.example.newsfit.domain.article.dto.GetArticle;
import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import com.example.newsfit.domain.article.repository.ArticleRepository;
import com.example.newsfit.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

import static com.example.newsfit.global.util.Utils.jsonObjectParser;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public GetArticle postArticle(String requestBody) throws ParseException {
        JSONObject jsonObject = jsonObjectParser(requestBody);

        String title = (String) jsonObject.get("title");
        String content = (String) jsonObject.get("content");
        Press press = Press.valueOf(((String) jsonObject.get("press")).toUpperCase());
        Category category = Category.valueOf(((String) jsonObject.get("category")).toUpperCase());

        Article article = Article.builder()
                .title(title)
                .content(content)
                .press(press)
                .category(category)
                .build();

        articleRepository.save(article);

        return GetArticle.of(article);
    }

    public List<Article> getArticles(String category, String press, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Category categoryEnum = null;
        Press pressEnum = null;

        if (!"allCategory".equalsIgnoreCase(category)) {
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
        System.out.println("categoryEnum = " + categoryEnum);

        if (!"allPress".equalsIgnoreCase(press)) {
            try {
                pressEnum = Press.valueOf(press.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
        System.out.println("pressEnum = " + pressEnum);

        if (categoryEnum == null && pressEnum == null) {
            return articleRepository.findAll(pageable).getContent();
        } else if (categoryEnum == null) {
            return articleRepository.findByPress(pressEnum, pageable);
        } else if (pressEnum == null) {
            return articleRepository.findByCategory(categoryEnum, pageable);
        } else {
            return articleRepository.findByCategoryAndPress(categoryEnum, pressEnum, pageable);
        }
    }

    public Boolean removeArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("기사를 찾을 수 없습니다."));

        articleRepository.delete(article);

        return true;
    }
}

