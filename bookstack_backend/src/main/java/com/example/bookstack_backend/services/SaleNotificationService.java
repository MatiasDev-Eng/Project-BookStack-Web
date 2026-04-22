package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.notification.SaleNotificationDTO;
import com.example.bookstack_backend.models.OrderItem;
import com.example.bookstack_backend.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifySeller(User seller, OrderItem item, String deliveryAddress) {
        SaleNotificationDTO notification = new SaleNotificationDTO(
                item.getBook().getTitle(),
                item.getQuantity(),
                LocalDateTime.now(),
                deliveryAddress
        );

        // Each seller has their own topic: /topic/seller.42, /topic/seller.7
        String destination = "/topic/seller." + seller.getId();
        messagingTemplate.convertAndSend(destination, notification);
    }
}
