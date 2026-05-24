package com.roadmap.dto.response;

import lombok.*;

@Data @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;
}