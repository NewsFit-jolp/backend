package com.example.newsfit.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecommenderUtils {

    private final RestTemplate restTemplate;

    @Value("${recommender.endpoint}")
    private String recommenderEndpoint;

    public Boolean registerMember(Long memberId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = String.format("{ \"user_id\": %d }", memberId);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                recommenderEndpoint + "/new-user",
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            return responseMap.get("status").equals("new user added");
        } else {
            throw new RuntimeException("Failed to send POST request: " + response.getStatusCode());
        }
    }

    public void deleteMember(Long memberId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        String requestBody = String.format("{ \"user_id\": %d }", memberId);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                recommenderEndpoint + "/delete-user",
                HttpMethod.DELETE,
                request,
                String.class
        );

        System.out.println("response.getBody() = " + response.getBody());

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to send Delete request: " + response.getStatusCode());
        }
    }

    public Boolean removeOldArticles(Long newsId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = String.format("{ \"news_id\": %d }", newsId);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

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
            throw new RuntimeException("Failed to send POST request: " + response.getStatusCode());
        }

    }
}
