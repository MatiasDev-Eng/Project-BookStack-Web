package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.dto.request.AddToCartRequest;
import com.example.bookstack_backend.dto.response.CartItemResponse;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.CartService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

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

        // Mock SecurityContext
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void testAddToCart_Success() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setBookId(1L);
        request.setQuantity(2);

        doNothing().when(cartService).addBookToCart(anyLong(), anyLong(), anyInt());

        mockMvc.perform(post("/api/cart/add/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book added to cart successfully"));
    }

    @Test
    void testGetCart_Success() throws Exception {
        CartItemResponse item = new CartItemResponse(1L, 1L, "Test Book", new BigDecimal("10.00"), 2);
        List<CartItemResponse> items = Arrays.asList(item);

        when(cartService.getCartItemsByUserId(anyLong())).thenReturn(items);

        mockMvc.perform(get("/api/cart/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void testRemoveItem_Success() throws Exception {
        doNothing().when(cartService).removeItem(anyLong());

        mockMvc.perform(delete("/api/cart/remove/1/"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateQuantity_Success() throws Exception {
        doNothing().when(cartService).updateQuantity(anyLong(), anyInt());

        mockMvc.perform(put("/api/cart/update-quantity/1/")
                        .param("quantity", "5"))
                .andExpect(status().isOk());
    }
}
