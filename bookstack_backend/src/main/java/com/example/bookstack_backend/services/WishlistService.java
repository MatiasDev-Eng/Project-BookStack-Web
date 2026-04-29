package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.WishlistItemResponse;
import com.example.bookstack_backend.models.*;
import com.example.bookstack_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ── Get wishlist items ────────────────────────────────────
    public List<WishlistItemResponse> getWishlistItems(User user) {
        return wishlistRepository.findByUser(user)
                .map(wishlist -> wishlist.getItems().stream()
                        .map(item -> new WishlistItemResponse(
                                item.getWishlistItemId(),
                                item.getBook().getBookId(),
                                item.getBook().getTitle(),
                                item.getBook().getAuthor(),
                                item.getBook().getPrice(),
                                item.getBook().getStock(),
                                item.getBook().getCoverImage() != null
                        )).toList())
                .orElse(Collections.emptyList());
    }

    // ── Add book to wishlist ──────────────────────────────────
    @Transactional
    public void addToWishlist(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseGet(() -> wishlistRepository.save(new Wishlist(user)));

        // Don't add duplicates
        boolean alreadyInWishlist = wishlist.getItems().stream()
                .anyMatch(item -> item.getBook().getBookId().equals(bookId));

        if (!alreadyInWishlist) {
            wishlist.getItems().add(new WishlistItem(wishlist, book));
            wishlistRepository.save(wishlist);
        }
    }

    // ── Remove item from wishlist ─────────────────────────────
    @Transactional
    public void removeFromWishlist(Long wishlistItemId) {
        wishlistItemRepository.deleteById(wishlistItemId);
    }

    // ── Move single item to cart ──────────────────────────────
    @Transactional
    public void moveToCart(User user, Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

        Book book = wishlistItem.getBook();

        // Get or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        // Add to cart if not already there, otherwise increment qty
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getBook().getBookId().equals(book.getBookId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + 1);
        } else {
            cart.getItems().add(new CartItem(cart, book, 1));
        }

        cartRepository.save(cart);

        // Remove from wishlist
        wishlistItemRepository.delete(wishlistItem);
    }

    // ── Move entire wishlist to cart ──────────────────────────
    @Transactional
    public void moveAllToCart(User user) {
        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        if (wishlist.getItems().isEmpty()) return;

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        wishlist.getItems().forEach(wishlistItem -> {
            Book book = wishlistItem.getBook();

            Optional<CartItem> existing = cart.getItems().stream()
                    .filter(item -> item.getBook().getBookId().equals(book.getBookId()))
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + 1);
            } else {
                cart.getItems().add(new CartItem(cart, book, 1));
            }
        });

        cartRepository.save(cart);

        // Clear wishlist
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
    }
}