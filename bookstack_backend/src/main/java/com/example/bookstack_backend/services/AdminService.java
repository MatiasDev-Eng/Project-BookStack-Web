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
}