
package com.roadmap.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    public String generateRoadmap(String prompt) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "Groq API key is missing. Check production configuration."
            );
        }

        WebClient webClient = webClientBuilder.build();

        Map<String, Object> body = Map.of(

            "model",
            "openai/gpt-oss-120b",

            "messages",
            List.of(

                Map.of(
                    "role", "system",
                    "content", """
                        You are an expert course roadmap generator.

                        Return ONLY valid JSON.
                        No markdown.
                        No explanations.
                        """
                ),

                Map.of(
                    "role", "user",
                    "content", """
                        Generate a complete course roadmap.

                        Return ONLY valid JSON.

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
            Map.of("type", "json_object"),

            "temperature",
            0.7,

            "max_completion_tokens",
            4096
        );

        try {

            Map<String, Object> response = webClient.post()

                .uri("https://api.groq.com/openai/v1/chat/completions")

                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + apiKey
                )

                .contentType(MediaType.APPLICATION_JSON)

                .bodyValue(body)

                .retrieve()

                .onStatus(
                    status -> status.isError(),
                    clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .defaultIfEmpty("No error details returned")
                        .flatMap(errorBody ->
                            Mono.error(new RuntimeException(
                                "Groq API Error: HTTP "
                                + clientResponse.statusCode()
                                + " - "
                                + errorBody
                            ))
                        )
                )

                .bodyToMono(
                    new ParameterizedTypeReference<Map<String, Object>>() {}
                )

                .block();

            if (response == null) {
                throw new RuntimeException(
                    "Empty response received from Groq API"
                );
            }

            Object choicesObject = response.get("choices");

            if (!(choicesObject instanceof List<?> choices)
                    || choices.isEmpty()) {
                throw new RuntimeException(
                    "No choices returned from Groq API"
                );
            }

            Map<?, ?> choice = (Map<?, ?>) choices.get(0);

            Map<?, ?> message =
                (Map<?, ?>) choice.get("message");

            if (message == null || message.get("content") == null) {
                throw new RuntimeException(
                    "No content returned from Groq API"
                );
            }

            return message.get("content").toString();

        } catch (WebClientResponseException e) {

            throw new RuntimeException(
                "Groq API HTTP Error: "
                + e.getStatusCode()
                + " - "
                + e.getResponseBodyAsString(),
                e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                "Course generation failed: " + e.getMessage(),
                e
            );
        }
    }
}
