package com.roadmap.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * This service generates roadmap steps.
 * Swap the template logic below with a real AI API call
 * (e.g., OpenAI, Google Gemini, or Anthropic Claude)
 * by adding the API key to application.properties and
 * making an HTTP POST to the provider's chat endpoint.
 */
@Service
public class AiRoadmapService {

    public List<Map<String, String>> generateSteps(String topic, String skillLevel, String duration) {
        // Template-based generation (replace with real AI call in production)
        return List.of(
                step("Week 1–2", topic + " Fundamentals", "Core concepts, setup, and first hands-on exercises."),
                step("Week 3–4", "Intermediate Concepts", "Deeper patterns, best practices, and labs."),
                step("Week 5–6", "Advanced Techniques", "Real-world scenarios and complex problem-solving."),
                step("Week 7–8", "Build a Portfolio Project", "Apply everything in a production-ready project."),
                step("Week 9–10", "Interview & Career Prep", "Mock interviews, resume tips, and job strategy.")
        );
    }

    private Map<String, String> step(String weekLabel, String title, String desc) {
        return Map.of("weekLabel", weekLabel, "title", title, "description", desc);
    }
}