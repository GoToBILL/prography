package com.example.prography_project.util;

import com.example.prography_project.user.domain.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FakerApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<User> fetchUsers(int seed, int quantity) {
        String url = "https://fakerapi.it/api/v1/users?_seed=" + seed + "&_quantity=" + quantity + "&_locale=ko_KR";
        String response = restTemplate.getForObject(url, String.class);

        List<User> users = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.get("data");

            for (JsonNode node : dataArray) {
                User user = new User(node.get("id").asLong(),node.get("username").asText(), node.get("email").asText());
                users.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Faker API 요청 실패", e);
        }

        return users;
    }
}