package com.example.newsfit.global.util;

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

        ResponseEntity<String> response = restTemplate.exchange(
                recommenderEndpoint + "/delete-news",
                HttpMethod.DELETE,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            return responseMap.get("status").equals("news deleted");
        } else {
            throw new RuntimeException("Failed to send DELETE request: " + response.getStatusCode());
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
}
