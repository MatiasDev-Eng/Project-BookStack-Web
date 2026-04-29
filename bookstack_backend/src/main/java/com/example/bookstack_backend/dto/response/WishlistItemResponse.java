package com.example.bookstack_backend.dto.response;

import java.math.BigDecimal;

public class WishlistItemResponse {
    private Long wishlistItemId;
    private Long bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stock;
    private boolean hasCover;

    public WishlistItemResponse(Long wishlistItemId, Long bookId, String title,
                                 String author, BigDecimal price, Integer stock, boolean hasCover) {
        this.wishlistItemId = wishlistItemId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.hasCover = hasCover;
    }

    public Long getWishlistItemId() { return wishlistItemId; }
    public Long getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public boolean isHasCover() { return hasCover; }
}