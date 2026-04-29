package com.example.bookstack_backend.services;

import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.OrderRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.example.bookstack_backend.repository.CartRepository;

import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Ban user
    public User banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsBanned(true);
        return userRepository.save(user);
    }

    // Unban user
    public User unbanUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsBanned(false);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nullify card references in orders before deleting cards
        user.getCreditCards().forEach(card -> {
            orderRepository.nullifyCardReferences(card.getCardId());
        });

        // Delete the user's cart (cart items cascade automatically)
        cartRepository.findByUser(user).ifPresent(cartRepository::delete);

        userRepository.delete(user);
    }
}