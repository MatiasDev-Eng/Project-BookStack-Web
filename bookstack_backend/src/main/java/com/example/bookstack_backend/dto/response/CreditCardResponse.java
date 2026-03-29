package com.example.bookstack_backend.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC) // Force public visibility
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CreditCardResponse {
    private Long cardId;
    private String cardNumber;
    private String cardHolderName;
    private LocalDate expirationDate;
    private String zipCode;
}
