package com.example.newsfit.domain.article.dto;

import lombok.Getter;

@Getter
public class LangChainRequest {
    private String article;

    public LangChainRequest(String article) {
        this.article = article;
    }
}
