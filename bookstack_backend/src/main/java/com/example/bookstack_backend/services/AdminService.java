package com.example.bookstack_backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookstack_backend.models.Admin;
import com.example.bookstack_backend.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public boolean login(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);

        if (adminOpt.isEmpty()) {
            return false;
        }

        Admin admin = adminOpt.get();

        // Simple password check (no hashing yet)
        return admin.getPassword().equals(password);
    }
}