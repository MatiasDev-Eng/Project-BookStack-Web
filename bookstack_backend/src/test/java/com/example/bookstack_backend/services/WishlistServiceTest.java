package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.WishlistItemResponse;
import com.example.bookstack_backend.models.*;
import com.example.bookstack_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private User user;
    private Book book;
    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPrice(BigDecimal.valueOf(12.99));
        book.setStock(5);

        wishlist = new Wishlist(user);
        wishlist.setWishlistId(1L);
    }

    @Test
    void getWishlistItems_ShouldReturnItems() {
        WishlistItem item = new WishlistItem(wishlist, book);
        item.setWishlistItemId(1L);
        wishlist.getItems().add(item);

        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));

        List<WishlistItemResponse> result = wishlistService.getWishlistItems(user);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        assertEquals("Test Author", result.get(0).getAuthor());
    }

    @Test
    void getWishlistItems_ShouldReturnEmpty_WhenNoWishlist() {
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.empty());

        List<WishlistItemResponse> result = wishlistService.getWishlistItems(user);

        assertTrue(result.isEmpty());
    }

    @Test
    void addToWishlist_ShouldAddItem_WhenNotAlreadyInWishlist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        wishlistService.addToWishlist(user, 1L);

        assertEquals(1, wishlist.getItems().size());
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    void addToWishlist_ShouldNotAddDuplicate() {
        WishlistItem existing = new WishlistItem(wishlist, book);
        wishlist.getItems().add(existing);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));

        wishlistService.addToWishlist(user, 1L);

        assertEquals(1, wishlist.getItems().size());
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void addToWishlist_ShouldCreateWishlist_WhenNoneExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(inv -> inv.getArgument(0));

        wishlistService.addToWishlist(user, 1L);

        verify(wishlistRepository, times(2)).save(any(Wishlist.class));
    }

    @Test
    void addToWishlist_ShouldThrow_WhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> wishlistService.addToWishlist(user, 99L));
    }

    @Test
    void removeFromWishlist_ShouldCallDelete() {
        wishlistService.removeFromWishlist(1L);

        verify(wishlistItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void moveToCart_ShouldMoveItemAndRemoveFromWishlist() {
        WishlistItem wishlistItem = new WishlistItem(wishlist, book);
        wishlistItem.setWishlistItemId(1L);

        Cart cart = new Cart(user);
        cart.setCartId(1L);

        when(wishlistItemRepository.findById(1L)).thenReturn(Optional.of(wishlistItem));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        wishlistService.moveToCart(user, 1L);

        assertEquals(1, cart.getItems().size());
        assertEquals(book, cart.getItems().get(0).getBook());
        verify(wishlistItemRepository, times(1)).delete(wishlistItem);
    }

    @Test
    void moveToCart_ShouldIncrementQty_WhenBookAlreadyInCart() {
        WishlistItem wishlistItem = new WishlistItem(wishlist, book);
        wishlistItem.setWishlistItemId(1L);

        Cart cart = new Cart(user);
        CartItem existingCartItem = new CartItem(cart, book, 2);
        cart.getItems().add(existingCartItem);

        when(wishlistItemRepository.findById(1L)).thenReturn(Optional.of(wishlistItem));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        wishlistService.moveToCart(user, 1L);

        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }

    @Test
    void moveAllToCart_ShouldMoveAllItemsAndClearWishlist() {
        WishlistItem item1 = new WishlistItem(wishlist, book);
        wishlist.getItems().add(item1);

        Book book2 = new Book();
        book2.setBookId(2L);
        book2.setTitle("Book 2");
        book2.setPrice(BigDecimal.valueOf(9.99));
        WishlistItem item2 = new WishlistItem(wishlist, book2);
        wishlist.getItems().add(item2);

        Cart cart = new Cart(user);

        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        wishlistService.moveAllToCart(user);

        assertEquals(2, cart.getItems().size());
        assertTrue(wishlist.getItems().isEmpty());
    }

    @Test
    void moveAllToCart_ShouldThrow_WhenNoWishlist() {
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> wishlistService.moveAllToCart(user));
    }
}