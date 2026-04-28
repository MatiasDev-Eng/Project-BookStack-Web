package com.example.bookstack_backend.dto;

import com.example.bookstack_backend.dto.request.*;
import com.example.bookstack_backend.dto.response.*;
import com.example.bookstack_backend.models.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DtoTest {

    @Test
    void testAddToCartRequest() {
        AddToCartRequest request = new AddToCartRequest();
        request.setBookId(1L);
        request.setQuantity(2);
        assertEquals(1L, request.getBookId());
        assertEquals(2, request.getQuantity());
    }

    @Test
    void testCreateBookRequest() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Title");
        request.setAuthor("Author");
        request.setPrice(BigDecimal.TEN);
        request.setStock(5);
        request.setCondition(ECondition.NEW);
        request.setIsbn("123");
        request.setOwnerId(1L);
        
        assertEquals("Title", request.getTitle());
        assertEquals("Author", request.getAuthor());
        assertEquals(BigDecimal.TEN, request.getPrice());
        assertEquals(5, request.getStock());
        assertEquals(ECondition.NEW, request.getCondition());
        assertEquals("123", request.getIsbn());
        assertEquals(1L, request.getOwnerId());
    }

    @Test
    void testLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");
        assertEquals("user", request.getUsername());
        assertEquals("pass", request.getPassword());
    }

    @Test
    void testSignUpRequest() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("user");
        request.setEmail("email");
        request.setPassword("pass");
        Set<String> roles = new HashSet<>();
        roles.add("role");
        request.setRole(roles);
        
        assertEquals("user", request.getUsername());
        assertEquals("email", request.getEmail());
        assertEquals("pass", request.getPassword());
        assertEquals(roles, request.getRole());
    }

    @Test
    void testOrderItemResponse() {
        Book book = new Book();
        book.setTitle("Book Title");
        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setQuantity(2);
        item.setPriceAtPurchase(BigDecimal.valueOf(15.0));

        OrderItemResponse response = new OrderItemResponse(item);
        assertEquals("Book Title", response.getBookTitle());
        assertEquals(2, response.getQuantity());
        assertEquals(BigDecimal.valueOf(15.0), response.getPriceAtPurchase());

        OrderItemResponse emptyResponse = new OrderItemResponse();
        emptyResponse.setBookTitle("Title");
        emptyResponse.setQuantity(1);
        emptyResponse.setPriceAtPurchase(BigDecimal.ONE);
        assertEquals("Title", emptyResponse.getBookTitle());
        assertEquals(1, emptyResponse.getQuantity());
        assertEquals(BigDecimal.ONE, emptyResponse.getPriceAtPurchase());
    }

    @Test
    void testMessageResponse() {
        MessageResponse response = new MessageResponse("Hello");
        assertEquals("Hello", response.getMessage());
        response.setMessage("World");
        assertEquals("World", response.getMessage());
    }
    
    @Test
    void testUserInfoResponse() {
        UserInfoResponse response = new UserInfoResponse(1L, "user", "email", "light");
        assertEquals(1L, response.getUserId());
        assertEquals("user", response.getUsername());
        assertEquals("email", response.getEmail());
        assertEquals("light", response.getThemePreference());

        response.setUserId(2L);
        response.setUsername("user2");
        response.setEmail("email2");
        assertEquals(2L, response.getUserId());
        assertEquals("user2", response.getUsername());
        assertEquals("email2", response.getEmail());
    }

    @Test
    void testUserDetailsResponse() {
        UserDetailsResponse response = new UserDetailsResponse(
                "user", "email", Collections.emptyList(), BigDecimal.TEN, "dark", 1L
        );
        assertEquals("user", response.getUsername());
        assertEquals("email", response.getEmail());
        assertEquals("[]", response.getAuthorities());
        assertEquals(BigDecimal.TEN, response.getBalance());
        assertEquals("dark", response.getThemePreference());
        assertEquals(1L, response.getId());
    }

    @Test
    void testBookResponse() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("owner");

        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Title");
        book.setOwner(owner);
        book.setPrice(BigDecimal.ONE);
        book.setCondition(ECondition.NEW);

        BookResponse response = new BookResponse(book);
        assertEquals(1L, response.getBookId());
        assertEquals("Title", response.getTitle());
        assertEquals(1L, response.getOwnerId());
        assertEquals("owner", response.getOwnerUsername());
        assertEquals("NEW", response.getCondition());
    }
}
