package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Book;

import java.math.BigDecimal;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String condition;
    private String description;


    private BigDecimal price;

    public BookResponse(Book book) {
        this.id = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
        this.price = book.getPrice();
        this.genre = book.getGenre();
        this.condition = book.getCondition() != null ? book.getCondition().name() : null;
        this.description = book.getDescription();

    }

    public BookResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCondition() {
    return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
