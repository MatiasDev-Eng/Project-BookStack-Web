package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.OrderDetailsResponse;
import com.example.bookstack_backend.dto.response.OrderResponse;
import com.example.bookstack_backend.models.*;
import com.example.bookstack_backend.repository.CartItemRepository;
import com.example.bookstack_backend.repository.CartRepository;
import com.example.bookstack_backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    public Order checkout(User user, String address) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Can't checkout an empty cart");
        }

        Order order = new Order();
        order.setUserOrder(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        order.setDeliveryAddress(address);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getBook().getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAllByCart(cart);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<OrderResponse> getAllOrdersForUser(User user) {
        List<Order> orders = orderRepository.findByUserOrder(user);

        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    public OrderDetailsResponse getOrderDetails(Long orderId, String username) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserOrder().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        return new OrderDetailsResponse(order);
    }

    public Order updateStatus(Long orderId, String username, OrderStatus status) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserOrder().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        if (status == OrderStatus.DELIVERED || status == OrderStatus.IN_TRANSIT) {
            throw new RuntimeException("Only administrators can set this status.");
        }

        order.setStatus(status);
        return orderRepository.save(order);

    }
}
