package com.roadmap.controller;

import com.roadmap.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final OpenAIService openAIService;

    @PostMapping("/generate-roadmap")
    public Map<String, String> generateRoadmap(
            @RequestBody Map<String, String> request
    ) {

        String goal = request.get("goal");

        String prompt = """
                Create a detailed learning roadmap for:
                %s

                Include:
                - beginner to advanced steps
                - technologies
                - projects
                - timeline
                - interview preparation
                """.formatted(goal);

        String response = openAIService.generateRoadmap(prompt);

        return Map.of(
                "roadmap", response
        );
    }
}