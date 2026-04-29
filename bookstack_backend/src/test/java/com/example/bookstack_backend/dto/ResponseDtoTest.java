package com.example.bookstack_backend.dto;

import com.example.bookstack_backend.dto.response.*;
import com.example.bookstack_backend.models.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseDtoTest {

    @Test
    void testBookResponse() {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("123");
        book.setPrice(BigDecimal.TEN);

        User owner = new User();
        owner.setId(1L);
        book.setOwner(owner);

        BookResponse response = new BookResponse(book);
        assertEquals(1L, response.getBookId());
        assertEquals("Title", response.getTitle());
        assertEquals("Author", response.getAuthor());
        assertEquals("123", response.getIsbn());
        assertEquals(BigDecimal.TEN, response.getPrice());

        BookResponse empty = new BookResponse();
        empty.setBookId(2L);
        empty.setTitle("T");
        empty.setAuthor("A");
        empty.setIsbn("I");
        empty.setPrice(BigDecimal.ONE);
        assertEquals(2L, empty.getBookId());
        assertEquals("T", empty.getTitle());
        assertEquals("A", empty.getAuthor());
        assertEquals("I", empty.getIsbn());
        assertEquals(BigDecimal.ONE, empty.getPrice());
    }

    @Test
    void testOrderResponse() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(BigDecimal.TEN);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        order.setDeliveryAddress("Address");
        order.setOrderItems(new ArrayList<>());

        OrderResponse response = new OrderResponse(order);
        assertEquals(1L, response.getId());
        assertEquals(BigDecimal.TEN, response.getTotalPrice());
        assertEquals(OrderStatus.ORDERED.name(), response.getStatus());
        assertEquals("Address", response.getDeliveryAddress());
        assertEquals(0, response.getItemCount());

        OrderResponse empty = new OrderResponse();
        empty.setId(2L);
        empty.setStatus("DELIVERED");
        assertEquals(2L, empty.getId());
        assertEquals("DELIVERED", empty.getStatus());
    }

    @Test
    void testOrderDetailsResponse() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(BigDecimal.TEN);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        order.setDeliveryAddress("Address");
        order.setOrderItems(new ArrayList<>());

        OrderDetailsResponse response = new OrderDetailsResponse(order);
        assertEquals(1L, response.getId());
        assertEquals(BigDecimal.TEN, response.getTotalPrice());
        assertEquals(OrderStatus.ORDERED.name(), response.getStatus());

        OrderDetailsResponse empty = new OrderDetailsResponse();
        empty.setId(2L);
        empty.setStatus("DELIVERED");
        assertEquals(2L, empty.getId());
        assertEquals("DELIVERED", empty.getStatus());
    }

    @Test
    void testCartItemResponse() {
        CartItemResponse response = new CartItemResponse(1L, 2L, "Title", BigDecimal.TEN, 3, 10);
        assertEquals(1L, response.getCartItemId());
        assertEquals(2L, response.getBookId());
        assertEquals("Title", response.getTitle());
        assertEquals(BigDecimal.TEN, response.getPrice());
        assertEquals(3, response.getQuantity());

        response.setCartItemId(4L);
        response.setBookId(5L);
        response.setTitle("T");
        response.setPrice(BigDecimal.ONE);
        response.setQuantity(6);
        assertEquals(4L, response.getCartItemId());
        assertEquals(5L, response.getBookId());
        assertEquals("T", response.getTitle());
        assertEquals(BigDecimal.ONE, response.getPrice());
        assertEquals(6, response.getQuantity());
    }

    @Test
    void testUserDetailsResponse() {
        UserDetailsResponse response = new UserDetailsResponse("user", "email", Collections.emptyList(), BigDecimal.TEN, "light", 1L);
        assertEquals("user", response.getUsername());
        assertEquals("email", response.getEmail());
        assertNotNull(response.getAuthorities());
        assertEquals("light", response.getThemePreference());
    }
}
