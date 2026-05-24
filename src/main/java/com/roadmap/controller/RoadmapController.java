package com.roadmap.controller;

import com.roadmap.dto.request.GenerateRoadmapRequest;
import com.roadmap.dto.response.RoadmapResponse;
import com.roadmap.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    // POST /api/roadmaps/generate
    @PostMapping("/generate")
    public ResponseEntity<RoadmapResponse> generate(
            @Valid @RequestBody GenerateRoadmapRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roadmapService.generateAndSave(request, userDetails.getUsername()));
    }

    // GET /api/roadmaps/my
    @GetMapping("/my")
    public ResponseEntity<List<RoadmapResponse>> getMyRoadmaps(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roadmapService.getMyRoadmaps(userDetails.getUsername()));
    }

    // PATCH /api/roadmaps/steps/{stepId}/toggle
    @PatchMapping("/steps/{stepId}/toggle")
    public ResponseEntity<Void> toggleStep(
            @PathVariable Long stepId,
            @AuthenticationPrincipal UserDetails userDetails) {
        roadmapService.toggleStep(stepId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // DELETE /api/roadmaps/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoadmap(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        roadmapService.deleteRoadmap(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}