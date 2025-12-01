package com.cashflow.controller;

import com.cashflow.dto.ApiResponse;
import com.cashflow.dto.UserDTO;
import com.cashflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for handling OAuth2 authentication
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OAuth2Controller {

    private final UserService userService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Handle OAuth2 login success
     * Called after successful Google authentication
     */
    @GetMapping("/oauth2/callback")
    public RedirectView oauth2Callback(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return new RedirectView(frontendUrl + "/login?error=oauth_failed");
        }

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");

        // Check if user exists, if not create new user
        UserDTO user = userService.findOrCreateOAuth2User(email, name, googleId);

        // Redirect to frontend with user info
        return new RedirectView(frontendUrl + "/oauth2/callback?userId=" + user.getId() +
                "&username=" + user.getUsername() + "&email=" + user.getEmail());
    }

    /**
     * Get current OAuth2 user info
     */
    @GetMapping("/api/oauth2/user")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentOAuth2User(
            @AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.ok(ApiResponse.error("No authenticated user"));
        }

        String email = oauth2User.getAttribute("email");
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }

    /**
     * Get Google login URL
     */
    @GetMapping("/api/oauth2/google-url")
    public ResponseEntity<ApiResponse<String>> getGoogleLoginUrl() {
        String googleLoginUrl = "/oauth2/authorization/google";
        return ResponseEntity.ok(ApiResponse.success("Google login URL", googleLoginUrl));
    }
}
