package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    void deleteByWishlist_WishlistId(Long wishlistId);
}