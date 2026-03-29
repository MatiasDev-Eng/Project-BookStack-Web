package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.CreateBookRequest;
import com.example.bookstack_backend.dto.request.CreateCreditCardRequest;
import com.example.bookstack_backend.dto.response.CreditCardResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.CreditCard;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@EnableAsync
@Slf4j
@RequestMapping("/api/payments/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @Operation(
            summary = "Saves credit card",
            description = "Takes credit card info and saves it for future use",
            tags = { "payments", "post" })
    @PostMapping("/add-card/")
    public ResponseEntity<CreditCardResponse> saveCreditCard(@RequestBody CreateCreditCardRequest request) {
        CreditCardResponse response =  paymentService.saveCreditCard(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Shows cards",
            description = "Displays credit card info to user",
            tags = { "payments", "get" })
    @GetMapping("/cards/")
    public ResponseEntity<List<CreditCardResponse>> getCreditCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("userDetails = {}", userDetails);
        List<CreditCardResponse> cards = paymentService.getCreditCards(userDetails.getId());
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve balance",
            description = "Simulates retrieval of balance for a user. Just sets it to zero",
            tags = { "payments", "POST" })
    @PostMapping("/retrieve-balance/")
    public ResponseEntity<BigDecimal> retrieveBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        BigDecimal balance = paymentService.retrieveBalance(userDetails.getId());
        return ResponseEntity.ok(balance);
    }
}
