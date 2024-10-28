package com.example.newsfit.domain.article.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

@DynamoDBTable(tableName = "url_ddb")
@Getter
@Setter
public class ArticleSource {

    @DynamoDBHashKey
    private String url;

    @DynamoDBAttribute
    private String content;

    @DynamoDBAttribute
    private String category;

    @DynamoDBAttribute
    private String image;

    @DynamoDBAttribute
    private String pubDate;

    @DynamoDBAttribute
    private String publisher;

    @DynamoDBAttribute
    private String title;
}
