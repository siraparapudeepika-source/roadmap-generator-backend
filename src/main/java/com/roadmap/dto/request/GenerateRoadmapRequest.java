package com.roadmap.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class GenerateRoadmapRequest {
    @NotBlank
    private String topic;
    @NotBlank
    private String skillLevel;   // Beginner / Intermediate / Advanced
    @NotBlank
    private String duration;     // "1 month", "3 months", etc.
    private List<String> goals;  // ["Get a job", "Freelancing", ...]
}