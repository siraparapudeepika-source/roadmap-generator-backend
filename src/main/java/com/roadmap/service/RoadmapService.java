package com.roadmap.service;

import com.roadmap.dto.request.GenerateRoadmapRequest;
import com.roadmap.dto.response.RoadmapResponse;
import com.roadmap.model.*;
import com.roadmap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapStepRepository stepRepository;
    private final UserProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final AiRoadmapService aiRoadmapService;

    @Transactional
    public RoadmapResponse generateAndSave(GenerateRoadmapRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        // Generate steps using AI service (or template logic)
        List<Map<String, String>> generatedSteps = aiRoadmapService.generateSteps(
                request.getTopic(), request.getSkillLevel(), request.getDuration()
        );

        Roadmap roadmap = Roadmap.builder()
                .title(request.getTopic() + " Roadmap")
                .category("Custom")
                .description("AI-generated roadmap for " + request.getTopic())
                .duration(request.getDuration())
                .skillLevel(request.getSkillLevel())
                .learningGoals(String.join(",", request.getGoals() != null ? request.getGoals() : List.of()))
                .aiGenerated(true)
                .published(false)
                .createdBy(user)
                .build();

        roadmapRepository.save(roadmap);

        List<RoadmapStep> steps = new ArrayList<>();
        for (int i = 0; i < generatedSteps.size(); i++) {
            Map<String, String> s = generatedSteps.get(i);
            RoadmapStep step = RoadmapStep.builder()
                    .roadmap(roadmap)
                    .title(s.get("title"))
                    .description(s.get("description"))
                    .weekLabel(s.get("weekLabel"))
                    .stepOrder(i + 1)
                    .build();
            steps.add(step);
        }
        stepRepository.saveAll(steps);
        roadmap.setSteps(steps);

        return toResponse(roadmap, user);
    }

    public List<RoadmapResponse> getMyRoadmaps(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        return roadmapRepository.findByCreatedBy(user).stream()
                .map(r -> toResponse(r, user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleStep(Long stepId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        RoadmapStep step = stepRepository.findById(stepId).orElseThrow();

        Optional<UserProgress> existing = progressRepository.findByUserAndStep(user, step);
        if (existing.isPresent()) {
            UserProgress p = existing.get();
            p.setCompleted(!p.isCompleted());
            p.setCompletedAt(p.isCompleted() ? java.time.LocalDateTime.now() : null);
            progressRepository.save(p);
        } else {
            progressRepository.save(UserProgress.builder()
                    .user(user).step(step).completed(true)
                    .completedAt(java.time.LocalDateTime.now())
                    .build());
        }
    }

    @Transactional
    public void deleteRoadmap(Long roadmapId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Roadmap roadmap = roadmapRepository.findById(roadmapId).orElseThrow();
        if (!roadmap.getCreatedBy().equals(user)) throw new RuntimeException("Not authorized");
        roadmapRepository.delete(roadmap);
    }

    private RoadmapResponse toResponse(Roadmap roadmap, User user) {
        List<UserProgress> progList = progressRepository.findByUser(user);
        Set<Long> completedStepIds = progList.stream()
                .filter(UserProgress::isCompleted)
                .map(p -> p.getStep().getId())
                .collect(Collectors.toSet());

        List<RoadmapResponse.StepResponse> steps = roadmap.getSteps() == null ? List.of() :
                roadmap.getSteps().stream().map(s -> RoadmapResponse.StepResponse.builder()
                                                     .id(s.getId())
                                                     .title(s.getTitle())
                                                     .description(s.getDescription())
                                                     .weekLabel(s.getWeekLabel())
                                                     .stepOrder(s.getStepOrder())
                                                     .completed(completedStepIds.contains(s.getId()))
                                                     .build()).collect(Collectors.toList());

        return RoadmapResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .category(roadmap.getCategory())
                .description(roadmap.getDescription())
                .duration(roadmap.getDuration())
                .rating(roadmap.getRating())
                .studentCount(roadmap.getStudentCount())
                .published(roadmap.isPublished())
                .skillLevel(roadmap.getSkillLevel())
                .steps(steps)
                .build();
    }
}