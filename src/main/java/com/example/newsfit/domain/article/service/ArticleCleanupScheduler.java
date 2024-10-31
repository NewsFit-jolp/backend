package com.example.newsfit.domain.article.service;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.repository.ArticleRepository;
import com.example.newsfit.global.util.RecommenderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleCleanupScheduler {

    @Autowired
    private final ArticleRepository articleRepository;
    private final RecommenderUtils recommenderUtils;

    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanup() throws JsonProcessingException {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Article> oldArticles = articleRepository.findOldArticle(yesterday);
        for (Article oldArticle : oldArticles) {
            recommenderUtils.removeOldArticles(oldArticle.getArticleId());
            articleRepository.delete(oldArticle);
        }
    }
}
