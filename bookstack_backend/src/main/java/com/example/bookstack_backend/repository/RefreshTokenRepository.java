package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.RefreshToken;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser_Id(Long userId);

    @Modifying
    int deleteByUser(User user);
}
