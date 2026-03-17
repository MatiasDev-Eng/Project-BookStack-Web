package com.example.bookstack_backend.dto.response;

import java.math.BigDecimal;

public class CartItemResponse {
    private Long cartItemId;
    private Long bookId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public CartItemResponse(Long cartItemId, Long bookId, String title, BigDecimal price, Integer quantity) {
        this.cartItemId = cartItemId;
        this.bookId = bookId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public Long getCartItemId() { return cartItemId; }
    public Long getBookId() { return bookId; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }

    // Setters
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}