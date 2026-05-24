package com.roadmap.service;

import com.roadmap.dto.response.*;
import com.roadmap.model.*;
import com.roadmap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;

    public AdminStatsResponse getStats() {
        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalRoadmaps(roadmapRepository.count())
                .publishedRoadmaps(roadmapRepository.countByPublished(true))
                .activeUsers(userRepository.countByActive(true))
                .build();
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void toggleRoadmapPublished(Long roadmapId) {
        Roadmap r = roadmapRepository.findById(roadmapId).orElseThrow();
        r.setPublished(!r.isPublished());
        roadmapRepository.save(r);
    }

    public void deleteRoadmap(Long roadmapId) {
        roadmapRepository.deleteById(roadmapId);
    }

    private UserResponse toUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId()).username(u.getName()).email(u.getEmail())
                .role(u.getRole().name()).active(u.isActive())
                .joinedAt(u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate().toString() : "")
                .build();
    }
}