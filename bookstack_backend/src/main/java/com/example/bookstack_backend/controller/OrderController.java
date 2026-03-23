package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.response.BookResponse;
import com.example.bookstack_backend.dto.response.OrderDetailsResponse;
import com.example.bookstack_backend.dto.response.OrderResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.Order;
import com.example.bookstack_backend.models.OrderStatus;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.OrderRepository;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.services.OrderService;
import com.example.bookstack_backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders/")
public class OrderController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Operation(
            summary = "Checkout cart",
            description = "(TAKES IN JWT) takes the current cart and its items and creates an order",
            tags = { "orders", "POST" })
    @PostMapping("/")
        public ResponseEntity<?> checkoutCart(@RequestParam String deliveryAddress) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            return userRepository.findByUsername(username)
                    .map(user -> {
                        try {
                            // Perform the checkout
                            Order completedOrder = orderService.checkout(user, deliveryAddress);

                            OrderResponse response = new OrderResponse(completedOrder);

                            return ResponseEntity.status(HttpStatus.CREATED).body(response);
                        } catch (IllegalStateException e) {
                            return ResponseEntity.badRequest().body(e.getMessage());
                        }
                    })
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

    @Operation(
            summary = "Get orders",
            description = "(TAKES IN JWT) shows details about all orders",
            tags = { "orders", "GET" })
    @GetMapping("/")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderResponse> orderList = orderService.getAllOrdersForUser(user);

        return ResponseEntity.ok(orderList);
    }

    @Operation(
            summary = "Get one order",
            description = "(TAKES IN JWT) shows details about one order",
            tags = { "orders", "GET" })
    @GetMapping("/{orderId}/")
    public ResponseEntity<?> getOneOrder(@PathVariable Long orderId) throws AccessDeniedException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderDetailsResponse response = orderService.getOrderDetails(orderId, username);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Updates order status",
            description = "(TAKES IN JWT) takes in orderId and new status",
            tags = { "orders", "PUT" })
    @PutMapping("/{orderId}/status/")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestParam OrderStatus status) throws AccessDeniedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Order updatedOrder = orderService.updateStatus(orderId, username, status);
            return ResponseEntity.ok(new OrderDetailsResponse(updatedOrder));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
