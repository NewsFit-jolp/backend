package com.example.newsfit.domain.article.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.newsfit.domain.article.entity.ArticleSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleSourceRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public ArticleSource findByUrl(String url) {
        return dynamoDBMapper.load(ArticleSource.class, url);
    }
}
