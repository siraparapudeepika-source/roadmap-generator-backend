package com.roadmap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "roadmap_step_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProgress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "roadmap_step_id", nullable = false)
    private RoadmapStep step;

    private boolean completed = false;
    private LocalDateTime completedAt;
}