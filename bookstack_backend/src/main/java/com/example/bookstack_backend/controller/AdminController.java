package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.dto.response.UserInfoResponse;
import com.example.bookstack_backend.services.BookService;
import com.example.bookstack_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.bookstack_backend.dto.request.AdminLoginRequest;
import com.example.bookstack_backend.dto.response.MessageResponse;
import com.example.bookstack_backend.services.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")

@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BookService bookService;

    @GetMapping("/users/")
    public ResponseEntity<?> getUnapprovedUsers() {
        List<UserInfoResponse> userList = adminService.getAllUnapprovedUsers();
        return ResponseEntity.ok(userList);
    }

    @PutMapping("/users/{userId}/approve/")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        adminService.approveUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/")
    public ResponseEntity<?> getUnapprovedBooks() {
        List<BookResponse> userList = adminService.getAllUnapprovedBooks();
        return ResponseEntity.ok(userList);
    }

    @PutMapping("/books/{bookId}/approve/")
    public ResponseEntity<?> approveBook(@PathVariable Long bookId) {
        adminService.approveBook(bookId);
        return ResponseEntity.ok().build();
    }


}