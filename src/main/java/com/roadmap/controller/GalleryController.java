package com.roadmap.controller;

import com.roadmap.dto.response.RoadmapResponse;
import com.roadmap.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    // GET /api/gallery?category=Web+Dev&search=react
    @GetMapping
    public ResponseEntity<List<RoadmapResponse>> getGallery(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(galleryService.getPublishedRoadmaps(category, search));
    }

    // GET /api/gallery/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RoadmapResponse> getRoadmap(@PathVariable Long id) {
        return ResponseEntity.ok(galleryService.getRoadmapById(id));
    }
}