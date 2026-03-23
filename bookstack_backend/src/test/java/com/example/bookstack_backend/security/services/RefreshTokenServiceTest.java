package com.example.bookstack_backend.security.services;

import com.example.bookstack_backend.exceptions.TokenRefreshException;
import com.example.bookstack_backend.models.RefreshToken;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.RefreshTokenRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 3600000L);
    }

    @Test
    void findByToken_ShouldReturnToken() {
        RefreshToken token = new RefreshToken();
        when(refreshTokenRepository.findByToken("test-token")).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.findByToken("test-token");

        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    void createRefreshToken_ShouldCreateAndSaveToken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(1L);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ShouldReturnToken_WhenNotExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
    }

    @Test
    void verifyExpiration_ShouldThrowException_WhenExpired() {
        RefreshToken token = new RefreshToken();
        token.setToken("expired-token");
        token.setExpiryDate(Instant.now().minusSeconds(60));

        assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    void deleteByUserId_ShouldCallRepository() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);

        int result = refreshTokenService.deleteByUserId(1L);

        assertEquals(1, result);
        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}
