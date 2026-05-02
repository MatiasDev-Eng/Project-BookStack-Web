package com.example.bookstack_backend.services;

import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.CartRepository;
import com.example.bookstack_backend.repository.OrderRepository;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBanUser() {
        User user = new User();
        user.setId(1L);
        user.setIsBanned(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User bannedUser = adminUserService.banUser(1L);

        assertTrue(bannedUser.getIsBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUnbanUser() {
        User user = new User();
        user.setId(1L);
        user.setIsBanned(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User unbannedUser = adminUserService.unbanUser(1L);

        assertFalse(unbannedUser.getIsBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        adminUserService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }
}
