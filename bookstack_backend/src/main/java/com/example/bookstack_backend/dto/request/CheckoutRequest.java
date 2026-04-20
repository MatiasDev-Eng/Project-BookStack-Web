package com.example.bookstack_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {
    private String deliveryAddress;
    private Long cardId;
}
