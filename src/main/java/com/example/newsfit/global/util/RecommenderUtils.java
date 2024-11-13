package com.example.newsfit.global.util;

import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import com.example.newsfit.domain.member.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommenderUtils {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${recommender.endpoint}")
    private String recommenderEndpoint;

    public void registerArticle(Article article) {
        try {
            JSONObject request = new JSONObject();
            request.put("news_id", article.getArticleId());
            request.put("category", article.getCategory().toString());
            request.put("publisher", article.getPress().toString());
            request.put("title", article.getTitle());
            request.put("headLine", article.getTitle());
            request.put("thumbnail", article.getImages().get(0));
            request.put("publishDate", article.getPublishDate().toString());

            String requestBody = request.toJSONString();

            requestRecommender(requestBody, "/new-news", HttpMethod.POST);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerMember(Long memberId) throws JsonProcessingException {
        String requestBody = String.format("{ \"user_id\": %d }", memberId);
        requestRecommender(requestBody, "/new-user", HttpMethod.POST);
    }

    public void deleteMember(Long memberId) throws JsonProcessingException {
        String requestBody = String.format("{ \"user_id\": %d }", memberId);
        requestRecommender(requestBody, "/delete-user", HttpMethod.DELETE);
    }

    public void removeOldArticles(Long newsId) throws JsonProcessingException {
        String requestBody = String.format("{ \"news_id\": %d }", newsId);
        requestRecommender(requestBody, "/delete-news", HttpMethod.DELETE);
    }

    public void putPreferredPress(Member member, JSONArray pressList) throws JsonProcessingException {
        List<Press> preferredPress = member.getPreferredPress();
        for (Press press : preferredPress) {
            if (!pressList.contains(press.name())) {
                String requestBody = String.format("{ \"user_id\": %d, \"publisher\": \"%s\", \"action\": \"%s\" }", member.getId(), press, "delete");
                requestRecommender(requestBody, "/user-publisher", HttpMethod.POST);
            }
        }
        for (Object press : pressList) {
            if (!preferredPress.contains(Press.valueOf(String.valueOf(press)))) {
                String requestBody = String.format("{ \"user_id\": %d, \"publisher\": \"%s\", \"action\": \"%s\" }", member.getId(), press, "add");
                requestRecommender(requestBody, "/user-publisher", HttpMethod.POST);
            }
        }
    }

    public void putPreferredCategories(Member member, JSONArray categoryList) throws JsonProcessingException {
        List<Category> preferredCategory = member.getPreferredCategories();
        for (Category category : preferredCategory) {
            if (!categoryList.contains(category.toString())) {
                String requestBody = String.format("{ \"user_id\": %d, \"category\": \"%s\", \"action\": \"%s\" }", member.getId(), category, "delete");
                requestRecommender(requestBody, "/user-category", HttpMethod.POST);
            }
        }
        for (Object category : categoryList) {
            if (!preferredCategory.contains(Category.fromDisplayName((String) category))) {
                String requestBody = String.format("{ \"user_id\": %d, \"category\": \"%s\", \"action\": \"%s\" }", member.getId(), category, "add");
                requestRecommender(requestBody, "/user-category", HttpMethod.POST);
            }
        }
    }

    public String rateArticle(Long articleId, Long memberId, int preference) throws JsonProcessingException {
        String requestBody = String.format("{ \"user_id\": %d, \"news_id\": \"%s\", \"preference\": \"%d\" }", memberId, articleId, preference);
        requestRecommender(requestBody, "/receive-feedback", HttpMethod.POST);
        return "success";
    }

    public String recommendArticles(Long memberId, int page, int pageSize) throws JsonProcessingException {
        String path = String.format("/recommend-news?userId=%d&page=%d&pageSize=%d", memberId, page, pageSize);
        return requestRecommender("", path, HttpMethod.GET);
    }

    private String requestRecommender(String requestBody, String path, HttpMethod method) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                recommenderEndpoint + path,
                method,
                request,
                String.class
        );

        if (HttpStatus.OK == response.getStatusCode()) {
            String responseBody = response.getBody();
            Object json = objectMapper.readValue(responseBody, Object.class);
            ObjectWriter ow = objectMapper.writerWithDefaultPrettyPrinter();
            return ow.writeValueAsString(json);
        } else {
            throw new RuntimeException("Failed to send request: " + response.getStatusCode());
        }
    }
}

