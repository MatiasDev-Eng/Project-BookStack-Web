package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.CartItemResponse;
import com.example.bookstack_backend.models.Book;
import com.example.bookstack_backend.models.Cart;
import com.example.bookstack_backend.models.CartItem;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.BookRepository;
import com.example.bookstack_backend.repository.CartItemRepository;
import com.example.bookstack_backend.repository.CartRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Book book;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.valueOf(19.99));

        cart = new Cart(user);
        cart.setCartId(1L);
    }

    @Test
    void addBookToCart_ShouldAddNewItem_WhenItemNotInCart() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        cartService.addBookToCart(1L, 1L, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(book, cart.getItems().get(0).getBook());
    }

    @Test
    void addBookToCart_ShouldUpdateQuantity_WhenItemAlreadyInCart() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        CartItem existingItem = new CartItem(cart, book, 1);
        cart.getItems().add(existingItem);

        cartService.addBookToCart(1L, 1L, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }

    @Test
    void addBookToCart_ShouldCreateCart_WhenCartDoesNotExist() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.empty());
        
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        when(cartRepository.save(cartCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        cartService.addBookToCart(1L, 1L, 2);

        verify(cartRepository, times(1)).save(any(Cart.class));
        Cart createdCart = cartCaptor.getValue();
        assertEquals(1, createdCart.getItems().size());
        assertEquals(2, createdCart.getItems().get(0).getQuantity());
    }

    @Test
    void addBookToCart_ShouldThrowException_WhenBookNotFound() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.addBookToCart(1L, 1L, 2));
    }

    @Test
    void getCartItemsByUserId_ShouldReturnItems() {
        CartItem item = new CartItem(cart, book, 2);
        item.setCartItemId(1L);
        cart.getItems().add(item);
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        List<CartItemResponse> result = cartService.getCartItemsByUserId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void removeItem_ShouldCallDelete() {
        when(cartItemRepository.existsById(1L)).thenReturn(true);

        cartService.removeItem(1L);

        verify(cartItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void removeItem_ShouldThrowException_WhenItemNotFound() {
        when(cartItemRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> cartService.removeItem(1L));
    }

    @Test
    void updateQuantity_ShouldUpdateQuantity() {
        CartItem item = new CartItem(cart, book, 1);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        cartService.updateQuantity(1L, 5);

        assertEquals(5, item.getQuantity());
        verify(cartItemRepository, times(1)).save(item);
    }

    @Test
    void updateQuantity_ShouldThrowException_WhenQuantityLessThanOne() {
        CartItem item = new CartItem(cart, book, 1);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity(1L, 0));
    }
}
