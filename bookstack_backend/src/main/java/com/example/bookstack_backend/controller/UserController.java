package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.UserDetailsResponse;
import com.example.bookstack_backend.repository.RoleRepository;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.jwt.JwtUtils;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User Details", description = "User Details API")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/me")
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
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Use userDetails.getId() or getUsername() as the owner
        UserDetailsResponse response = new UserDetailsResponse(
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities());

        return ResponseEntity.ok(response);

    }
}
