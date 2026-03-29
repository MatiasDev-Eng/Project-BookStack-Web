package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Order;
import com.example.bookstack_backend.models.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private int itemCount;
    private String status;
    private String deliveryAddress;

    // Added fields
    private String cardHolderName;
    private String cardNumber;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderDate = order.getOrderDate();
        this.itemCount = (order.getOrderItems() != null) ? order.getOrderItems().size() : 0;
        this.status = order.getStatus().name();
        this.deliveryAddress = order.getDeliveryAddress();

        // Populate card info if it exists
        if (order.getCreditCard() != null) {
            this.cardHolderName = order.getCreditCard().getCardHolderName();
            this.cardNumber = maskCardNumber(order.getCreditCard().getCardNumber());
        }
    }

    public OrderResponse() {}

    // Helper to mask the card number for the UI (e.g., **** 5678)
    private String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) return "****";
        return "**** " + fullNumber.substring(fullNumber.length() - 4);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
}