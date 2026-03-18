package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.AddToCartRequest;
import com.example.bookstack_backend.dto.response.CartItemResponse;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.CartService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart/")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired private EntityManager entityManager;

    @PostMapping("/add/")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        // Pass the ID instead of the User object
        cartService.addBookToCart(userDetails.getId(), request.getBookId(), request.getQuantity());

        return ResponseEntity.ok("Book added to cart successfully");
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CartItemResponse> items = cartService.getCartItemsByUserId(userDetails.getId());
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/remove/{itemId}/")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-quantity/{itemId}/")
    public ResponseEntity<?> updateQuantity(@PathVariable Long itemId, @RequestParam Integer quantity) {
        cartService.updateQuantity(itemId, quantity);
        return ResponseEntity.ok().build();
    }
}