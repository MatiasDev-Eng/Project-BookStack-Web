package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.request.CreateCreditCardRequest;
import com.example.bookstack_backend.dto.response.CreditCardResponse;
import com.example.bookstack_backend.models.CreditCard;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.PaymentRepository;
import com.example.bookstack_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    // register card to user
    public CreditCardResponse saveCreditCard(CreateCreditCardRequest request) {
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        CreditCard savedCard = CreditCard.builder()
                .owner(owner)
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .expirationDate(request.getExpirationDate())
                .cvv(request.getCvv())
                .zipCode(request.getZipCode())
                .build();

        paymentRepository.save(savedCard);

        CreditCardResponse response = new CreditCardResponse();
        response.setCardId(savedCard.getCardId());
        response.setCardNumber(savedCard.getCardNumber());
        response.setCardHolderName(savedCard.getCardHolderName());
        response.setExpirationDate(savedCard.getExpirationDate());


        return response;
    }

    public List<CreditCardResponse> getCreditCards(Long userId) {
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        log.info("Before call to repository");
        List<CreditCard> cards = paymentRepository.findByOwnerId(userId);
        log.info("Found {} cards", cards.size());

        return cards.stream()
                .map(card -> CreditCardResponse.builder()
                        .cardId(card.getCardId())
                        .cardNumber(maskCardNumber(card.getCardNumber())) // Security best practice
                        .cardHolderName(card.getCardHolderName())
                        .expirationDate(card.getExpirationDate())
                        .zipCode(card.getZipCode())
                        .build())
                .collect(Collectors.toList());
    }

    // Helper to mask numbers for the frontend
    private String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) return "****";
        return "**** **** **** " + fullNumber.substring(fullNumber.length() - 4);
    }

    public BigDecimal retrieveBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal balance = user.getBalance();
        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);

        return balance;
    }



    // "Retrieve payment" (set value to zero)

    // update seller balance.
}
