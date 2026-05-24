package com.roadmap.service;

import com.roadmap.dto.response.RoadmapResponse;
import com.roadmap.model.Roadmap;
import com.roadmap.repository.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final RoadmapRepository roadmapRepository;

    public List<RoadmapResponse> getPublishedRoadmaps(String category, String search) {
        List<Roadmap> roadmaps;
        if (search != null && !search.isBlank()) {
            roadmaps = roadmapRepository.findByPublishedTrueAndTitleContainingIgnoreCase(search);
        } else if (category != null && !category.equals("All")) {
            roadmaps = roadmapRepository.findByPublishedTrueAndCategory(category);
        } else {
            roadmaps = roadmapRepository.findByPublishedTrue();
        }
        return roadmaps.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RoadmapResponse getRoadmapById(Long id) {
        Roadmap r = roadmapRepository.findById(id).orElseThrow();
        return toResponse(r);
    }

    private RoadmapResponse toResponse(Roadmap r) {
        List<RoadmapResponse.StepResponse> steps = r.getSteps() == null ? List.of() :
                r.getSteps().stream().map(s -> RoadmapResponse.StepResponse.builder()
                                               .id(s.getId()).title(s.getTitle()).description(s.getDescription())
                                               .weekLabel(s.getWeekLabel()).stepOrder(s.getStepOrder()).completed(false)
                                               .build()).collect(Collectors.toList());

        return RoadmapResponse.builder()
                .id(r.getId()).title(r.getTitle()).category(r.getCategory())
                .description(r.getDescription()).duration(r.getDuration())
                .rating(r.getRating()).studentCount(r.getStudentCount())
                .published(r.isPublished()).steps(steps).build();
    }
}