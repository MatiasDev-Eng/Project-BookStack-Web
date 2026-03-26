package com.example.bookstack_backend.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ModelsTest {

    @Test
    void testBook() {
        User owner = new User();
        Book book = new Book(owner, 10, BigDecimal.TEN, "Title", "Author", ECondition.NEW, "123");
        book.setBookId(1L);
        book.setGenre("Genre");
        book.setEdition("Edition");
        book.setReviewCount(5);
        book.setTotalScore(4.5f);
        book.setPublishedYear(2020);
        book.setDescription("Desc");
        book.setIsActive(true);
        book.setCoverImageUrl("url");
        LocalDateTime now = LocalDateTime.now();
        book.setDatePosted(now);

        assertEquals(1L, book.getBookId());
        assertEquals(owner, book.getOwner());
        assertEquals(10, book.getStock());
        assertEquals(BigDecimal.TEN, book.getPrice());
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals(ECondition.NEW, book.getCondition());
        assertEquals("123", book.getIsbn());
        assertEquals("Genre", book.getGenre());
        assertEquals("Edition", book.getEdition());
        assertEquals(5, book.getReviewCount());
        assertEquals(4.5f, book.getTotalScore());
        assertEquals(2020, book.getPublishedYear());
        assertEquals("Desc", book.getDescription());
        assertTrue(book.getIsActive());
        assertEquals("url", book.getCoverImageUrl());
        assertEquals(now, book.getDatePosted());
        
        Book empty = new Book();
        assertNull(empty.getBookId());
    }

    @Test
    void testCart() {
        User user = new User();
        Cart cart = new Cart(user);
        cart.setCartId(1L);
        cart.setItems(new ArrayList<>());
        
        assertEquals(1L, cart.getCartId());
        assertEquals(user, cart.getUser());
        assertEquals(0, cart.getItems().size());
        
        cart.setUser(null);
        assertNull(cart.getUser());
    }

    @Test
    void testCartItem() {
        Cart cart = new Cart();
        Book book = new Book();
        CartItem item = new CartItem(cart, book, 2);
        item.setCartItemId(1L);
        
        assertEquals(1L, item.getCartItemId());
        assertEquals(cart, item.getCart());
        assertEquals(book, item.getBook());
        assertEquals(2, item.getQuantity());
        
        item.setQuantity(3);
        assertEquals(3, item.getQuantity());
        item.setCart(null);
        item.setBook(null);
        assertNull(item.getCart());
        assertNull(item.getBook());
    }

    @Test
    void testOrder() {
        User user = new User();
        Order order = new Order(user, BigDecimal.TEN);
        order.setId(1L);
        order.setStatus(OrderStatus.ORDERED);
        order.setDeliveryAddress("Address");
        order.setOrderItems(new ArrayList<>());
        order.setOrderDate(LocalDateTime.now());

        assertEquals(1L, order.getId());
        assertEquals(user, order.getUserOrder());
        assertEquals(BigDecimal.TEN, order.getTotalPrice());
        assertEquals(OrderStatus.ORDERED, order.getStatus());
        assertEquals("Address", order.getDeliveryAddress());
        assertNotNull(order.getOrderDate());
        assertEquals(0, order.getOrderItems().size());
        
        order.setUserOrder(null);
        order.setOrderItems(null);
        order.setTotalPrice(null);
        order.setStatus(null);
        order.setDeliveryAddress(null);
        order.setOrderDate(null);
        assertNull(order.getUserOrder());
        assertNull(order.getOrderItems());
        assertNull(order.getTotalPrice());
        assertNull(order.getStatus());
        assertNull(order.getDeliveryAddress());
        assertNull(order.getOrderDate());
        
        Order empty = new Order();
        assertNull(empty.getId());
    }

    @Test
    void testOrderItem() {
        Order order = new Order();
        Book book = new Book();
        OrderItem item = new OrderItem(order, book, BigDecimal.TEN, 2);
        item.setOrderItemId(1L);

        assertEquals(1L, item.getOrderItemId());
        assertEquals(order, item.getOrder());
        assertEquals(book, item.getBook());
        assertEquals(2, item.getQuantity());
        assertEquals(BigDecimal.TEN, item.getPriceAtPurchase());
        
        item.setQuantity(3);
        item.setPriceAtPurchase(BigDecimal.ONE);
        item.setOrder(null);
        item.setBook(null);
        assertEquals(3, item.getQuantity());
        assertEquals(BigDecimal.ONE, item.getPriceAtPurchase());
        assertNull(item.getOrder());
        assertNull(item.getBook());
        
        OrderItem empty = new OrderItem();
        assertNull(empty.getOrderItemId());
    }

    @Test
    void testReport() {
        User reporter = new User();
        Report report = new Report(reporter, "Expl");
        report.setReportId(1L);
        report.setIsResolved(true);
        LocalDateTime now = LocalDateTime.now();
        report.setDateReported(now);

        assertEquals(1L, report.getReportId());
        assertEquals(reporter, report.getReporter());
        assertEquals("Expl", report.getExplanation());
        assertTrue(report.getIsResolved());
        assertEquals(now, report.getDateReported());
        
        report.setReporter(null);
        report.setExplanation(null);
        report.setDateReported(null);
        report.setIsResolved(null);
        assertNull(report.getReporter());
        assertNull(report.getExplanation());
        assertNull(report.getDateReported());
        assertNull(report.getIsResolved());
        
        Report empty = new Report();
        assertFalse(empty.getIsResolved());
        assertNotNull(empty.getDateReported());
    }

    @Test
    void testRefreshToken() {
        RefreshToken token = new RefreshToken();
        token.setId(1L);
        token.setToken("token");
        token.setExpiryDate(Instant.now());
        User user = new User();
        token.setUser(user);

        assertEquals(1L, token.getId());
        assertEquals("token", token.getToken());
        assertNotNull(token.getExpiryDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testUser() {
        User user = new User("username", "email", "pass");
        user.setId(1L);
        user.setRealName("Name");
        user.setAddress("Addr");
        user.setProfilePictureUrl("url");
        user.setIsBanned(true);
        user.setRoles(new HashSet<>());
        LocalDateTime now = LocalDateTime.now();
        user.setDateCreated(now);

        assertEquals(1L, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("email", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals("Name", user.getRealName());
        assertEquals("Addr", user.getAddress());
        assertEquals("url", user.getProfilePictureUrl());
        assertTrue(user.getIsBanned());
        assertEquals(0, user.getRoles().size());
        assertEquals(now, user.getDateCreated());
        
        User empty = new User();
        assertNull(empty.getId());
    }

    @Test
    void testRole() {
        Role role = new Role(ERole.ROLE_ADMIN);
        role.setId(1);
        assertEquals(1, role.getId());
        assertEquals(ERole.ROLE_ADMIN, role.getName());
        
        role.setName(ERole.ROLE_USER);
        assertEquals(ERole.ROLE_USER, role.getName());
    }
}
