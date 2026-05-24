package com.roadmap.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    public String generateRoadmap(String prompt) {

        WebClient webClient =
                webClientBuilder.build();

        Map<String, Object> body =
                Map.of(

                        "model",
                        "llama-3.3-70b-versatile",

                        "messages",
                        List.of(

                                Map.of(
                                        "role",
                                        "system",

                                        "content",
                                        """
                                        Return ONLY valid JSON.
                                        No markdown.
                                        No explanations.
                                        """
                                ),

                                Map.of(
                                        "role",
                                        "user",

                                        "content",
                                        """
                                        Generate a complete course roadmap.
        
                                        Return ONLY valid JSON.
        
                                        NO markdown.
                                        NO explanations.
                                        NO code blocks.
        
                                        Use this exact structure:
        
                                        {
                                          "title": "string",
                                          "description": "string",
                                          "modules": [
                                            {
                                              "title": "string",
                                              "description": "string",
                                              "lessons": [
                                                {
                                                  "title": "string",
                                                  "description": "string",
                                                  "lesson_type": "video",
                                                  "duration_minutes": 30
                                                }
                                              ]
                                            }
                                          ]
                                        }
        
                                        User Request:
                                        """ + prompt
                                )
                        ),

                        "response_format",
                        Map.of(
                                "type",
                                "json_object"
                        ),

                        "temperature",
                        0.7,

                        "max_tokens",
                        4096
                );

        Map response = webClient.post()

                .uri(
                        "https://api.groq.com/openai/v1/chat/completions"
                )

                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )

                .contentType(MediaType.APPLICATION_JSON)

                .bodyValue(body)

                .retrieve()

                .bodyToMono(Map.class)

                .block();

        List choices =
                (List) response.get("choices");

        Map choice =
                (Map) choices.get(0);

        Map message =
                (Map) choice.get("message");

        return message.get("content").toString();
    }
}