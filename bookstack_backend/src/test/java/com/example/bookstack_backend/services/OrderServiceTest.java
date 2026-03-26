package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.OrderDetailsResponse;
import com.example.bookstack_backend.dto.response.OrderResponse;
import com.example.bookstack_backend.models.*;
import com.example.bookstack_backend.repository.CartItemRepository;
import com.example.bookstack_backend.repository.CartRepository;
import com.example.bookstack_backend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Book book;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.valueOf(10.00));

        cart = new Cart(user);
        cartItem = new CartItem(cart, book, 2);
        cart.getItems().add(cartItem);
    }

    @Test
    void checkout_ShouldCreateOrder_WhenCartIsNotEmpty() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        Order result = orderService.checkout(user, "123 Test St");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20.00), result.getTotalPrice());
        assertEquals(OrderStatus.ORDERED, result.getStatus());
        verify(cartItemRepository, times(1)).deleteAllByCart(cart);
        verify(cartRepository, times(1)).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void checkout_ShouldThrowException_WhenCartNotFound() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.checkout(user, "123 Test St"));
    }

    @Test
    void checkout_ShouldThrowException_WhenCartIsEmpty() {
        cart.getItems().clear();
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        assertThrows(RuntimeException.class, () -> orderService.checkout(user, "123 Test St"));
    }

    @Test
    void getAllOrdersForUser_ShouldReturnOrders() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderItems(new ArrayList<>());
        when(orderRepository.findByUserOrder(user)).thenReturn(Arrays.asList(order));

        List<OrderResponse> result = orderService.getAllOrdersForUser(user);

        assertEquals(1, result.size());
    }

    @Test
    void getOrderDetails_ShouldReturnDetails_WhenAuthorized() throws AccessDeniedException {
        Order order = new Order();
        order.setId(1L);
        order.setUserOrder(user);
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderItems(new ArrayList<>());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDetailsResponse result = orderService.getOrderDetails(1L, "testuser");

        assertNotNull(result);
    }

    @Test
    void getOrderDetails_ShouldThrowException_WhenUnauthorized() {
        Order order = new Order();
        order.setId(1L);
        order.setUserOrder(user);
        order.setStatus(OrderStatus.ORDERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> orderService.getOrderDetails(1L, "wronguser"));
    }

    @Test
    void updateStatus_ShouldUpdateStatus_WhenAuthorizedAndValidStatus() throws AccessDeniedException {
        Order order = new Order();
        order.setId(1L);
        order.setUserOrder(user);
        order.setStatus(OrderStatus.ORDERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateStatus(1L, "testuser", OrderStatus.RETURNED);

        assertEquals(OrderStatus.RETURNED, result.getStatus());
    }

    @Test
    void updateStatus_ShouldThrowException_WhenRestrictedStatusSetByNonAdmin() {
        Order order = new Order();
        order.setId(1L);
        order.setUserOrder(user);
        order.setStatus(OrderStatus.ORDERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.updateStatus(1L, "testuser", OrderStatus.DELIVERED));
        assertThrows(RuntimeException.class, () -> orderService.updateStatus(1L, "testuser", OrderStatus.IN_TRANSIT));
    }
}
