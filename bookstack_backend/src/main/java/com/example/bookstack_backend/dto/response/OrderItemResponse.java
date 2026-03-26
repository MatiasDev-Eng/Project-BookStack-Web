package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.OrderItem;

import java.math.BigDecimal;

public class OrderItemResponse {
    private String bookTitle;
    private Integer quantity;
    private BigDecimal priceAtPurchase;

    public OrderItemResponse(OrderItem item) {
        this.bookTitle = item.getBook().getTitle();
        this.quantity = item.getQuantity();
        this.priceAtPurchase = item.getPriceAtPurchase();
    }

    public OrderItemResponse() {
    }

    // Getters and Setters
    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }
}
