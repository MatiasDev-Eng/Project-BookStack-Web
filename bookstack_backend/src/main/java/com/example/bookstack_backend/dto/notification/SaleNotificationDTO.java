package com.example.bookstack_backend.dto.notification;

import java.time.LocalDateTime;

public record SaleNotificationDTO(
        String bookTitle,
        int quantity,
        LocalDateTime purchasedAt,
        String deliveryAddress
) {}
