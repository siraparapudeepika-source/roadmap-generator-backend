package com.roadmap.repository;

import com.roadmap.model.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {
    List<RoadmapStep> findByRoadmapIdOrderByStepOrder(Long roadmapId);
}