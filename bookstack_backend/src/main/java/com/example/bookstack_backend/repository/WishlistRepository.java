package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser(User user);
    Optional<Wishlist> findByUser_Id(Long userId);
}