package com.example.bookstack_backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id"})
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private int rating;

    private LocalDateTime createdAt;

    public Review() {}

    public Review(User user, Book book, int rating) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }
    // getters/setters
}
