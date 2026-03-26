package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Book;

import java.math.BigDecimal;

public class EditBookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String edition;
    private Integer publishedYear;
    private BigDecimal price;
    private Integer stock;
    private String condition;
    private String description;
    private String coverImageUrl;
    private boolean active;
    private Long ownerId; // We only send the ID, not the full User object

    // Conversion Constructor: Maps the Entity to the Response DTO
    public EditBookResponse(Book book) {
        this.id = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
        this.genre = book.getGenre();
        this.edition = book.getEdition();
        this.publishedYear = book.getPublishedYear();
        this.price = book.getPrice();
        this.stock = book.getStock();
        this.condition = book.getCondition() != null ? book.getCondition().toString() : null;
        this.description = book.getDescription();
        this.coverImageUrl = book.getCoverImageUrl();
        this.active = book.getIsActive(); // Matches your approval logic

        if (book.getOwner() != null) {
            this.ownerId = book.getOwner().getId();
        }
    }
}