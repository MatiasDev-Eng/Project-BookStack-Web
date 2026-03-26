package com.example.bookstack_backend.dto.request;

import com.example.bookstack_backend.models.ECondition;
import java.math.BigDecimal;

public class CreateBookRequest {
    private Long ownerId;
    private Integer stock;
    private BigDecimal price;
    private String title;
    private String author;
    private String genre;
    private String edition;
    private ECondition condition;
    private Integer publishedYear;
    private String description;
    private String isbn;
    private String coverImageUrl; // Handles the High Resolution Cover Image 

    // Getters and Setters
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    public ECondition getCondition() { return condition; }
    public void setCondition(ECondition condition) { this.condition = condition; }
    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
