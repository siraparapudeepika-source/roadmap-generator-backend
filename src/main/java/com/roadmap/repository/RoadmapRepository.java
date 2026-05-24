package com.roadmap.repository;

import com.roadmap.model.Roadmap;
import com.roadmap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByPublishedTrue();
    List<Roadmap> findByPublishedTrueAndCategory(String category);
    List<Roadmap> findByPublishedTrueAndTitleContainingIgnoreCase(String title);
    List<Roadmap> findByCreatedBy(User user);
    long countByPublished(boolean published);
}