package com.roadmap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public String generateRoadmap(String prompt) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "Groq API key is missing."
            );
        }

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                "Course generation prompt cannot be empty."
            );
        }

        // Lesson JSON schema
        Map<String, Object> lessonSchema = Map.of(
            "type", "object",

            "properties", Map.of(
                "title", Map.of("type", "string"),
                "description", Map.of("type", "string"),
                "lesson_type", Map.of(
                    "type", "string",
                    "enum", List.of("video")
                ),
                "duration_minutes", Map.of(
                    "type", "integer"
                )
            ),

            "required", List.of(
                "title",
                "description",
                "lesson_type",
                "duration_minutes"
            ),

            "additionalProperties", false
        );

        // Module JSON schema
        Map<String, Object> moduleSchema = Map.of(
            "type", "object",

            "properties", Map.of(
                "title", Map.of("type", "string"),
                "description", Map.of("type", "string"),
                "lessons", Map.of(
                    "type", "array",
                    "items", lessonSchema
                )
            ),

            "required", List.of(
                "title",
                "description",
                "lessons"
            ),

            "additionalProperties", false
        );

        // Complete course JSON schema
        Map<String, Object> courseSchema = Map.of(
            "type", "object",

            "properties", Map.of(
                "title", Map.of("type", "string"),
                "description", Map.of("type", "string"),
                "modules", Map.of(
                    "type", "array",
                    "items", moduleSchema
                )
            ),

            "required", List.of(
                "title",
                "description",
                "modules"
            ),

            "additionalProperties", false
        );

        // Groq request body
        Map<String, Object> body = Map.of(

            "model", "openai/gpt-oss-120b",

            "messages", List.of(

                Map.of(
                    "role", "system",
                    "content", """
                        You are an expert course curriculum designer.

                        Generate complete and practical course roadmaps.

                        Return a JSON object matching the supplied schema.
                        Do not return a JSON string.
                        Do not use markdown or code blocks.
                        Follow the requested number of modules.
                        Give each module relevant lessons.
                        Keep all descriptions informative and concise.
                        """
                ),

                Map.of(
                    "role", "user",
                    "content", """
                        Generate a complete course roadmap based on
                        the following user requirements.

                        Important:
                        - Follow the requested number of modules.
                        - Include relevant lessons in every module.
                        - Every lesson must have a title and description.
                        - Set lesson_type to video.
                        - Set duration_minutes to an integer.
                        - Return only the JSON object.

                        User Request:
                        """ + prompt
                )
            ),

            // Strict JSON schema enforcement
            "response_format", Map.of(
                "type", "json_schema",

                "json_schema", Map.of(
                    "name", "course_roadmap",
                    "strict", true,
                    "schema", courseSchema
                )
            ),

            "reasoning_effort", "low",

            "temperature", 0.7,

            "max_completion_tokens", 8192
        );

        try {

            WebClient webClient = webClientBuilder.build();

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

            if (response == null) {
                throw new RuntimeException(
                    "Empty response received from Groq."
                );
            }

            List choices = (List) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException(
                    "Groq returned no course-generation results."
                );
            }

            Map choice = (Map) choices.get(0);

            Map message = (Map) choice.get("message");

            if (message == null || message.get("content") == null) {
                throw new RuntimeException(
                    "Groq returned empty course content."
                );
            }

            String content = message.get("content").toString();

            // Validate JSON before returning it
            objectMapper.readTree(content);

            return content;

        } catch (WebClientResponseException e) {

            throw new RuntimeException(
                "Groq API Error: HTTP "
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
