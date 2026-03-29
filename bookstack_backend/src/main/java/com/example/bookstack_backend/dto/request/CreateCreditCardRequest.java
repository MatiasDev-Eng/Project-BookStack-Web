package com.example.bookstack_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateCreditCardRequest {
    private Long userId; // who the credit card belongs to
    private String cardNumber;
    private String cardHolderName;
    private LocalDate expirationDate;
    private Integer cvv;
    private String zipCode;
}
