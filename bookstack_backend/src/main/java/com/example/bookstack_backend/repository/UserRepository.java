package com.example.bookstack_backend.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bookstack_backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isBanned = true")
    List<User> findAllByIsBanned();

    List<User> findByIsBannedFalse();
    List<User> findByIsBannedTrue();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isBanned = false WHERE u.id = :userId")
    void approveUser(@Param("userId") Long userId);

    Optional<User> findByApiKey(String apiKey);

}
