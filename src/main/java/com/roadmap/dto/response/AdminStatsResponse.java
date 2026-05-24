package com.roadmap.dto.response;

import lombok.*;

@Data @AllArgsConstructor @Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalRoadmaps;
    private long publishedRoadmaps;
    private long activeUsers;
}