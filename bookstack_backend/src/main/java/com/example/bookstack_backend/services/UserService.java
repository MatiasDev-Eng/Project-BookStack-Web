package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.request.UpdateUserDetailsRequest;
import com.example.bookstack_backend.dto.response.MessageResponse;
import com.example.bookstack_backend.dto.response.UserInfoResponse;
import com.example.bookstack_backend.exceptions.UserNotFoundException;
import com.example.bookstack_backend.models.ERole;
import com.example.bookstack_backend.models.Role;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.RoleRepository;
import com.example.bookstack_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Transactional()
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User registerUser(String username, String email, String password, Set<String> strRoles) {
        // Create new user's account
        User user = new User(username, email,
                encoder.encode(password));

        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        user.setIsBanned(true); // admin will set this to false in their logs page.
        userRepository.save(user);

        return user;
    }


}
