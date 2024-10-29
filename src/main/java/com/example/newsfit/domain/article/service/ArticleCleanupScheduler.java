package com.example.newsfit.domain.article.service;

import com.example.newsfit.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ArticleCleanupScheduler {

    @Autowired
    private final ArticleRepository articleRepository;

    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanup() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        articleRepository.deleteOldArticle(yesterday);
    }
}
