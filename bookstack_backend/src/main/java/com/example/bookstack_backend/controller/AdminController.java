package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.dto.response.UserInfoResponse;
import com.example.bookstack_backend.models.AuditLog;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.EActionType;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import com.example.bookstack_backend.dto.request.AdminLoginRequest;
import com.example.bookstack_backend.dto.response.MessageResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")

@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminBookService adminBookService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private BookService bookService;

    @Autowired
    AuditLogService logService;

    @Autowired
    private UserService userService;

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

    @DeleteMapping("/users/{userId}/reject/")
    public ResponseEntity<?> rejectUser(@PathVariable Long userId) {
        try {
            adminUserService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error rejecting user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/users/active/")
    public ResponseEntity<?> getActiveUsers() {
        return ResponseEntity.ok(adminService.getAllActiveUsers());
    }

    @GetMapping("/users/frozen/")
    public ResponseEntity<?> getFrozenUsers() {
        return ResponseEntity.ok(adminService.getAllFrozenUsers());
    }

    @PutMapping("/users/{userId}/freeze/")
    public ResponseEntity<?> freezeUser(@PathVariable Long userId) {
        adminService.freezeUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/unfreeze/")
    public ResponseEntity<?> unfreezeUser(@PathVariable Long userId) {
        adminService.unfreezeUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user.getProfilePicture() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(user.getProfilePictureType()))
                    .body(user.getProfilePicture());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/books/{id}/cover")
    public ResponseEntity<byte[]> getBookCover(@PathVariable Long id) {
        Book book = bookService.findBookByIdIgnoreActive(id);

        if (book.getCoverImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(book.getCoverImageType()))
                .body(book.getCoverImage());
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

    @DeleteMapping("/books/{bookId}/reject/")
    public ResponseEntity<?> rejectBook(@PathVariable Long bookId) {
        adminBookService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/active/")
    public ResponseEntity<?> getActiveBooks() {
        return ResponseEntity.ok(adminService.getAllActiveBooks());
    }

    @GetMapping("/books/frozen/")
    public ResponseEntity<?> getFrozenBooks() {
        return ResponseEntity.ok(adminService.getAllFrozenBooks());
    }

    @PutMapping("/books/{bookId}/freeze/")
    public ResponseEntity<?> freezeBook(@PathVariable Long bookId) {
        adminService.freezeBook(bookId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/books/{bookId}/unfreeze/")
    public ResponseEntity<?> unfreezeBook(@PathVariable Long bookId) {
        adminService.unfreezeBook(bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs/")
    public ResponseEntity<List<AuditLog>> getAllLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) EActionType action) {

        if (userId != null) return ResponseEntity.ok(logService.getLogsByUser(userId));
        if (action != null) return ResponseEntity.ok(logService.getLogsByAction(action));
        return ResponseEntity.ok(logService.getAllLogs());
    }

}