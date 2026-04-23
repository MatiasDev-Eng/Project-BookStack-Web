package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.ECondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookResponse {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String edition;
    private BigDecimal price;
    private Integer stock;
    private Integer publishedYear;
    private String description;
    private ECondition condition;
    private Float totalScore;
    private Integer reviewCount;
    private Boolean isActive;
    private LocalDateTime datePosted;
    private boolean hasCover;
    private Long ownerId;
    private String ownerUsername;

    public BookResponse(Book book) {
        this.bookId        = book.getBookId();
        this.title         = book.getTitle();
        this.author        = book.getAuthor();
        this.isbn          = book.getIsbn();
        this.genre         = book.getGenre();
        this.edition       = book.getEdition();
        this.price         = book.getPrice();
        this.stock         = book.getStock();
        this.publishedYear = book.getPublishedYear();
        this.description   = book.getDescription();
        this.condition     = book.getCondition();
        this.totalScore    = book.getTotalScore();
        this.reviewCount   = book.getReviewCount();
        this.isActive      = book.getIsActive();
        this.datePosted    = book.getDatePosted();
        this.hasCover      = book.getCoverImage() != null;
        this.ownerId       = book.getOwner().getId();
        this.ownerUsername = book.getOwner().getUsername();
    }

    public BookResponse() {}

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ECondition getCondition() { return condition; }
    public void setCondition(ECondition condition) { this.condition = condition; }

    public Float getTotalScore() { return totalScore; }
    public void setTotalScore(Float totalScore) { this.totalScore = totalScore; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; }

    public boolean isHasCover() { return hasCover; }
    public void setHasCover(boolean hasCover) { this.hasCover = hasCover; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
}
