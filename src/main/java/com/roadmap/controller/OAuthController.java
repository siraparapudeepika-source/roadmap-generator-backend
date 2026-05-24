package com.roadmap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OAuthController {

    @GetMapping("/api/auth/oauth2/success")
    public Map<String, String> success() {

        return Map.of(
                "message", "Google Login Successful"
        );
    }
}