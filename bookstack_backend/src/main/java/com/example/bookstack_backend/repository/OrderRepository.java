package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.Order;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrder(User userOrder);

    @Modifying
    @Query("UPDATE Order o SET o.creditCard = null WHERE o.creditCard.cardId = :cardId")
    void nullifyCardReferences(@Param("cardId") Long cardId);
}