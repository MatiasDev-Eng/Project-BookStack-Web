package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.UserDetailsResponse;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.RoleRepository;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.jwt.JwtUtils;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;



@Tag(name = "User Details", description = "User Details API")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/me")
@Slf4j
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;



    @Operation(
            summary = "Get user info",
            description = "Obtains information about the user and returns it to the frontend",
            tags = { "user auth", "get" })
    @GetMapping("/")
    public ResponseEntity<UserDetailsResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsResponse response = new UserDetailsResponse(
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities(),
                user.getBalance(),
                user.getThemePreference()
        );

        return ResponseEntity.ok(response);

    }
    @PostMapping("/theme")
    public ResponseEntity<?> updateTheme(@RequestBody Map<String, String> body) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String theme = body.get("theme");

        if (!theme.equals("light") && !theme.equals("dark")) {
                return ResponseEntity.badRequest().body("Invalid theme value");
        }

        user.setThemePreference(theme);
        userRepository.save(user);

        return ResponseEntity.ok("Theme updated");
    }
}
