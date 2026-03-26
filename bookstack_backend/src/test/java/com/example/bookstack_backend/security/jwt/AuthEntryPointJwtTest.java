package com.example.bookstack_backend.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class AuthEntryPointJwtTest {

    @Test
    void commence_ShouldSetResponse() throws IOException, ServletException {
        AuthEntryPointJwt entryPoint = new AuthEntryPointJwt();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(authException.getMessage()).thenReturn("Unauthorized error");
        when(request.getServletPath()).thenReturn("/api/test");
        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(outputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
    }
}
