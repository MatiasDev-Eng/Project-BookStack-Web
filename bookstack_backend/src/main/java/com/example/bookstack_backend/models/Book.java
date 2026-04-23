package com.example.bookstack_backend.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    private Integer stock;
    private BigDecimal price;
    private String title;
    private String author;
    private String genre;
    private String edition;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    private ECondition condition;

    private Integer reviewCount;
    private Float totalScore;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean isActive;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    private LocalDateTime datePosted;
    private String isbn;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "cover_image")
    private byte[] coverImage;

    @Column(name = "cover_image_type")
    private String coverImageType;

    public Book() {
    }

    public Book(User owner, Integer stock, BigDecimal price, String title, String author, ECondition condition, String isbn) {
        this.owner = owner;
        this.stock = stock;
        this.price = price;
        this.title = title;
        this.author = author;
        this.condition = condition;
        this.isbn = isbn;
        this.datePosted = LocalDateTime.now();
        this.isActive = false;
        this.reviewCount = 0;
        this.totalScore = 0.0f;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public ECondition getCondition() {
        return condition;
    }

    public void setCondition(ECondition condition) {
        this.condition = condition;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Float totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCoverImage(byte[] coverImage) { this.coverImage = coverImage; }

    public byte[] getCoverImage() {return coverImage; }

    public void setCoverImageType(String coverImageType) { this.coverImageType = coverImageType; }
    public String getCoverImageType() { return coverImageType; }
}