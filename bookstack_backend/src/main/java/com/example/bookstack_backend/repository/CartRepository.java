package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.Cart;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_Id(Long userId);

    Optional<Cart> findByUser(User user);
}