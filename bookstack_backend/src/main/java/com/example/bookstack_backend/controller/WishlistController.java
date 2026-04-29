package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.WishlistItemResponse;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Get wishlist ──────────────────────────────────────────
    @GetMapping("/")
    public ResponseEntity<List<WishlistItemResponse>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlistItems(getCurrentUser()));
    }

    // ── Add to wishlist ───────────────────────────────────────
    @PostMapping("/add/")
    public ResponseEntity<?> addToWishlist(@RequestBody Map<String, Long> body) {
        Long bookId = body.get("bookId");
        if (bookId == null) return ResponseEntity.badRequest().body("bookId is required");
        wishlistService.addToWishlist(getCurrentUser(), bookId);
        return ResponseEntity.ok().build();
    }

    // ── Remove from wishlist ──────────────────────────────────
    @DeleteMapping("/remove/{wishlistItemId}/")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long wishlistItemId) {
        wishlistService.removeFromWishlist(wishlistItemId);
        return ResponseEntity.ok().build();
    }

    // ── Move single item to cart ──────────────────────────────
    @PostMapping("/move-to-cart/{wishlistItemId}/")
    public ResponseEntity<?> moveToCart(@PathVariable Long wishlistItemId) {
        wishlistService.moveToCart(getCurrentUser(), wishlistItemId);
        return ResponseEntity.ok().build();
    }

    // ── Move all to cart ──────────────────────────────────────
    @PostMapping("/move-all-to-cart/")
    public ResponseEntity<?> moveAllToCart() {
        wishlistService.moveAllToCart(getCurrentUser());
        return ResponseEntity.ok().build();
    }
}