package com.roadmap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roadmap_steps")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RoadmapStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String weekLabel;  // e.g. "Week 1-2"
    private Integer stepOrder;
}