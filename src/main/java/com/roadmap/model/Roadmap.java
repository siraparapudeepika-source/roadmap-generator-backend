package com.roadmap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "roadmaps")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Roadmap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String duration;
    private Double rating = 0.0;
    private Integer studentCount = 0;
    private boolean published = false;

    // For AI-generated roadmaps: who generated it
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private boolean aiGenerated = false;
    private String skillLevel;      // Beginner, Intermediate, Advanced
    private String learningGoals;   // comma-separated

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<RoadmapStep> steps;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }
}