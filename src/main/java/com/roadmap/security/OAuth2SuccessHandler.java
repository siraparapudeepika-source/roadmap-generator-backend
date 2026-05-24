package com.roadmap.security;

import com.roadmap.model.User;
import com.roadmap.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .provider("google")
                    .providerId(googleId)
                    .role(User.Role.USER)
                    .active(true)
                    .build();

            userRepository.save(user);

            System.out.println("User saved successfully: " + email);
        }

        response.sendRedirect("/api/auth/oauth2/success");
    }
}