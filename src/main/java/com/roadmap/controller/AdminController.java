package com.roadmap.controller;
import com.roadmap.dto.response.UserResponse;


import com.roadmap.dto.response.*;
import com.roadmap.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // GET /api/admin/stats
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // PATCH /api/admin/users/{id}/toggle-active
    @PatchMapping("/users/{id}/toggle-active")
    public ResponseEntity<Void> toggleUserActive(@PathVariable Long id) {
        adminService.toggleUserActive(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/admin/roadmaps/{id}/toggle-published
    @PatchMapping("/roadmaps/{id}/toggle-published")
    public ResponseEntity<Void> togglePublished(@PathVariable Long id) {
        adminService.toggleRoadmapPublished(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/admin/roadmaps/{id}
    @DeleteMapping("/roadmaps/{id}")
    public ResponseEntity<Void> deleteRoadmap(@PathVariable Long id) {
        adminService.deleteRoadmap(id);
        return ResponseEntity.noContent().build();
    }
}