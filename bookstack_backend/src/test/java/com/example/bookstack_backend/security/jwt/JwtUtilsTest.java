package com.example.bookstack_backend.security.jwt;

import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String jwtSecret = "testSecretKeyTestSecretKeyTestSecretKeyTestSecretKeyTestSecretKeyTestSecretKeyTestSecretKeyTestSecretKey";
    private final int jwtExpirationMs = 3600000;
    private final String jwtCookieName = "testCookie";
    private final String jwtRefreshCookieName = "testRefreshCookie";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
        ReflectionTestUtils.setField(jwtUtils, "jwtCookie", jwtCookieName);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshCookie", jwtRefreshCookieName);
    }

    @Test
    void generateTokenFromUsername_ShouldReturnValidToken() {
        String username = "testuser";
        // Pass empty authorities for the test
        String token = jwtUtils.generateTokenFromUsername(username, Collections.emptyList());

        assertNotNull(token);
        assertEquals(username, jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtToken_ShouldReturnTrueForValidToken() {
        String token = jwtUtils.generateTokenFromUsername("testuser", Collections.emptyList());
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalidToken"));
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForExpiredToken() {
        // Set expiration to a past time
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        String token = jwtUtils.generateTokenFromUsername("testuser", Collections.emptyList());
        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void generateJwtCookie_FromUserDetails_ShouldReturnCookie() {
        // UserDetailsImpl already has a getAuthorities() method
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@email.com", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);

        assertNotNull(cookie);
        assertEquals(jwtCookieName, cookie.getName());
        assertNotNull(cookie.getValue());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void generateJwtCookie_FromUser_ShouldReturnCookie() {
        // Assuming your User model has a getRoles() method that might be empty here
        User user = new User("testuser", "test@email.com", "password");
        user.setRoles(Collections.emptySet()); // Ensure roles are initialized

        ResponseCookie cookie = jwtUtils.generateJwtCookie(user);

        assertNotNull(cookie);
        assertEquals(jwtCookieName, cookie.getName());
    }

    @Test
    void generateRefreshJwtCookie_ShouldReturnCookie() {
        ResponseCookie cookie = jwtUtils.generateRefreshJwtCookie("refreshTokenValue");

        assertNotNull(cookie);
        assertEquals(jwtRefreshCookieName, cookie.getName());
        assertEquals("refreshTokenValue", cookie.getValue());
    }

    @Test
    void getJwtFromCookies_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie(jwtCookieName, "jwtValue");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = jwtUtils.getJwtFromCookies(request);

        assertEquals("jwtValue", result);
    }

    @Test
    void getJwtRefreshFromCookies_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie(jwtRefreshCookieName, "refreshValue");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = jwtUtils.getJwtRefreshFromCookies(request);

        assertEquals("refreshValue", result);
    }

    @Test
    void getCleanCookies_ShouldReturnEmptyCookies() {
        ResponseCookie cleanJwt = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefresh = jwtUtils.getCleanJwtRefreshCookie();

        // ResponseCookie returns null or empty string for value when "cleaned"
        assertTrue(cleanJwt.getValue() == null || cleanJwt.getValue().isEmpty());
        assertTrue(cleanRefresh.getValue() == null || cleanRefresh.getValue().isEmpty());
    }
}