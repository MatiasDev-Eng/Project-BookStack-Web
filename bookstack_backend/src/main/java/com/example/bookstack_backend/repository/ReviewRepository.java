package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.Review;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserAndBook(User user, Book book);
}
