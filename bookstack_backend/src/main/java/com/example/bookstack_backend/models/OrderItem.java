package com.example.bookstack_backend.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private BigDecimal priceAtPurchase;
    private Integer quantity;

    public OrderItem(Order order, Book book, BigDecimal priceAtPurchase, Integer quantity) {
        this.order = order;
        this.book = book;
        this.priceAtPurchase = priceAtPurchase;
        this.quantity = quantity;
    }

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
