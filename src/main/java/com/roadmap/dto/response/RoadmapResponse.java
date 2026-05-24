package com.roadmap.dto.response;

import lombok.*;
import java.util.List;

@Data @AllArgsConstructor @Builder
public class RoadmapResponse {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String duration;
    private Double rating;
    private Integer studentCount;
    private boolean published;
    private String skillLevel;
    private List<StepResponse> steps;

    @Data @AllArgsConstructor @Builder
    public static class StepResponse {
        private Long id;
        private String title;
        private String description;
        private String weekLabel;
        private Integer stepOrder;
        private boolean completed;  // user-specific, populated when user is logged in
    }
}