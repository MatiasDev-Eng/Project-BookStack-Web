package com.example.bookstack_backend.dto.response;

import com.example.bookstack_backend.models.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailsResponse {
    private Long id;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
    private String status;
    private String deliveryAddress;

    public OrderDetailsResponse() {
    }

    public OrderDetailsResponse(Order order) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderDate = order.getOrderDate();
        this.items = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .toList();
        this.status = order.getStatus().name();
        this.deliveryAddress = order.getDeliveryAddress();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}
