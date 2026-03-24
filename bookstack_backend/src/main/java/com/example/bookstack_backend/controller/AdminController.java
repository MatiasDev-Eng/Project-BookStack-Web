package com.example.bookstack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstack_backend.dto.request.AdminLoginRequest;
import com.example.bookstack_backend.dto.response.MessageResponse;
import com.example.bookstack_backend.services.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")

public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        boolean success = adminService.login(request.getUsername(), request.getPassword());

        if (!success) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid admin credentials"));
        }

        return ResponseEntity.ok(new MessageResponse("Admin login successful"));
    }
}
