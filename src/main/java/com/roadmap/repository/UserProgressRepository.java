package com.roadmap.repository;

import com.roadmap.model.UserProgress;
import com.roadmap.model.User;
import com.roadmap.model.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUser(User user);
    Optional<UserProgress> findByUserAndStep(User user, RoadmapStep step);
    long countByUserAndCompleted(User user, boolean completed);
}