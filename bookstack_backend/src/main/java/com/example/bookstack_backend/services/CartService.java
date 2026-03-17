package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.CartItemResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.Cart;
import com.example.bookstack_backend.models.CartItem;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.CartItemRepository;
import com.example.bookstack_backend.repository.CartRepository;
import com.example.bookstack_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public void addBookToCart(Long userId, Long bookId, Integer quantity) {
        // 1. Get a reference to the User (No DB hit yet)
        User user = userRepository.getReferenceById(userId);

        // 2. Find the cart or create a new one
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });

        // 3. Find the book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // 4. Check if book is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getBookId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            // No need for explicit save if @Transactional is working on the managed entity
        } else {
            CartItem newItem = new CartItem(cart, book, quantity);
            cart.getItems().add(newItem);
            // cartRepository.save(cart) handles the items via CascadeType.ALL
        }
    }

    public List<CartItemResponse> getCartItemsByUserId(Long userId) {
        return cartRepository.findByUser_Id(userId)
                .map(cart -> cart.getItems().stream()
                        .map(item -> new CartItemResponse(
                                item.getCartItemId(),
                                item.getBook().getBookId(),
                                item.getBook().getTitle(),
                                item.getBook().getPrice(),
                                item.getQuantity()
                        )).toList())
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void removeItem(Long itemId) {
        // Check if it exists first to avoid silent failures
        if (!cartItemRepository.existsById(itemId)) {
            throw new RuntimeException("Cart item not found with id: " + itemId);
        }
        cartItemRepository.deleteById(itemId);
    }

    @Transactional
    public void updateQuantity(Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Ensure quantity doesn't go below 1
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }
}
