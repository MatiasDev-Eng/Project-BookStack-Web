package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.response.WishlistItemResponse;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private jakarta.persistence.EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
        userDetails = UserDetailsImpl.build(user);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    void testGetWishlist_Success() throws Exception {
        WishlistItemResponse item = new WishlistItemResponse(
                1L, 1L, "Test Book", "Test Author",
                new BigDecimal("12.99"), 5, false);
        List<WishlistItemResponse> items = Arrays.asList(item);

        when(wishlistService.getWishlistItems(any(User.class))).thenReturn(items);

        mockMvc.perform(get("/api/wishlist/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void testAddToWishlist_Success() throws Exception {
        doNothing().when(wishlistService).addToWishlist(any(User.class), anyLong());

        mockMvc.perform(post("/api/wishlist/add/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("bookId", 1L))))
                .andExpect(status().isOk());
    }

    @Test
    void testAddToWishlist_MissingBookId() throws Exception {
        mockMvc.perform(post("/api/wishlist/add/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveFromWishlist_Success() throws Exception {
        doNothing().when(wishlistService).removeFromWishlist(anyLong());

        mockMvc.perform(delete("/api/wishlist/remove/1/"))
                .andExpect(status().isOk());
    }

    @Test
    void testMoveToCart_Success() throws Exception {
        doNothing().when(wishlistService).moveToCart(any(User.class), anyLong());

        mockMvc.perform(post("/api/wishlist/move-to-cart/1/"))
                .andExpect(status().isOk());
    }

    @Test
    void testMoveAllToCart_Success() throws Exception {
        doNothing().when(wishlistService).moveAllToCart(any(User.class));

        mockMvc.perform(post("/api/wishlist/move-all-to-cart/"))
                .andExpect(status().isOk());
    }
}