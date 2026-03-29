package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.dto.response.CreditCardResponse;
import com.example.bookstack_backend.models.CreditCard;
import com.example.bookstack_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findCreditCardByCardId(Long cardId);
    List<CreditCard> findByOwner(User owner);
    List<CreditCard> findByOwnerId(Long userId);
}
