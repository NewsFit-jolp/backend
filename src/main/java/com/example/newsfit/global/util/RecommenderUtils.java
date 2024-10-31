package com.example.newsfit.global.util;

import com.example.newsfit.domain.article.entity.Category;
import com.example.newsfit.domain.article.entity.Press;
import com.example.newsfit.domain.member.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommenderUtils {

    private final RestTemplate restTemplate;

    @Value("${recommender.endpoint}")
    private String recommenderEndpoint;

    public void registerMember(Long memberId) throws JsonProcessingException {
        String requestBody = String.format("{ \"user_id\": %d }", memberId);
        requestRecommender(requestBody, "new-user", HttpMethod.POST);
    }

    public void deleteMember(Long memberId) throws JsonProcessingException {
        String requestBody = String.format("{ \"user_id\": %d }", memberId);
        requestRecommender(requestBody, "delete-user", HttpMethod.DELETE);
    }

    public void removeOldArticles(Long newsId) throws JsonProcessingException {
        String requestBody = String.format("{ \"news_id\": %d }", newsId);
        requestRecommender(requestBody, "delete-news", HttpMethod.DELETE);
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

    private void requestRecommender(String requestBody, String path, HttpMethod method) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                recommenderEndpoint + path,
                method,
                request,
                String.class
        );

        if (HttpStatus.OK != response.getStatusCode()) {
            throw new RuntimeException("Failed to send request: " + response.getStatusCode());
        }
    }

    public String rateArticle(Long articleId, Long memberId, int preference) throws JsonProcessingException {
        String requestBody = String.format("{ \"user_id\": %d, \"news_id\": \"%s\", \"preference\": \"%d\" }", memberId, articleId, preference);
        requestRecommender(requestBody, "/receive-feedback", HttpMethod.POST);
        return "success";
    }
}
