package com.example.bookstack_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.dto.response.UserInfoResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookstack_backend.models.Admin;
import com.example.bookstack_backend.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AdminUserService adminUserService;

    public List<UserInfoResponse> getAllUnapprovedUsers() {
        List<User> users = userRepository.findAllByIsBanned();

        // convert into UserInfoResponse
        return users.stream()
                .map(user -> {
                    return new UserInfoResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getThemePreference()
                    );
                })
                .collect(Collectors.toList());
        // return
    }

    public List<BookResponse> getAllUnapprovedBooks() {
        List<Book> books = bookRepository.findByIsActiveFalse();

        // convert into UserInfoResponse
        return books.stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
        // return
    }

    public void approveUser(Long userId) {
        userRepository.approveUser(userId);
    }

    public void approveBook(Long bookId) {
        bookRepository.approveBook(bookId);
    }

    public List<UserInfoResponse> getAllActiveUsers() {
        return userRepository.findByIsBannedFalse().stream()
                .map(user -> new UserInfoResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getThemePreference()
                ))
                .collect(Collectors.toList());
    }

    public List<UserInfoResponse> getAllFrozenUsers() {
        return userRepository.findByIsBannedTrue().stream()
                .map(user -> new UserInfoResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getThemePreference()
                ))
                .collect(Collectors.toList());
    }

    public List<BookResponse> getAllActiveBooks() {
        return bookRepository.findByIsActiveTrueAndIsDeletedFalse().stream()
                .filter(book -> !Boolean.TRUE.equals(book.getIsFrozen()))
                .filter(book -> !Boolean.TRUE.equals(book.getIsDeleted()))
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    public List<BookResponse> getAllFrozenBooks() {
        return bookRepository.findByIsFrozenTrue().stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    public void freezeUser(Long userId) {
        adminUserService.banUser(userId);
    }

    public void unfreezeUser(Long userId) {
        adminUserService.unbanUser(userId);
    }

    public void freezeBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setIsFrozen(true);
        bookRepository.save(book);
    }

    public void unfreezeBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setIsFrozen(false);
        bookRepository.save(book);
    }
}