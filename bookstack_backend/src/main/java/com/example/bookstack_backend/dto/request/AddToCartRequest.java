package com.example.bookstack_backend.dto.request;

public class AddToCartRequest {
    private Long bookId;
    private Integer quantity;

    // Getters and Setters
    public Long getBookId() {return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

}