package com.example.bookstack_backend.controller;

import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.RoleRepository;
import com.example.bookstack_backend.repository.UserRepository;
import com.example.bookstack_backend.security.jwt.JwtUtils;
import com.example.bookstack_backend.security.services.UserDetailsImpl;
import com.example.bookstack_backend.services.AdminUserService;
import com.example.bookstack_backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

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
        when(authentication.getName()).thenReturn("testuser");

        // Common mocks
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
    }

    @Test
    void testGetUserInfo_Success() throws Exception {
        mockMvc.perform(get("/api/me/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateTheme_Success() throws Exception {
        mockMvc.perform(post("/api/me/theme")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"theme\": \"dark\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Theme updated"));
    }

    @Test
    void testUpdateTheme_InvalidTheme() throws Exception {
        mockMvc.perform(post("/api/me/theme")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"theme\": \"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid theme value"));
    }

    @Test
    void testUploadProfilePicture_Success() throws Exception {
        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/me/1/profile-picture")
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile picture updated."));
    }

    @Test
    void testUploadProfilePicture_NotAnImage() throws Exception {
        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/me/1/profile-picture")
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File must be an image."));
    }

    @Test
    void testGetCover_Success() throws Exception {
        user.setProfilePicture("image content".getBytes());
        user.setProfilePictureType("image/png");

        mockMvc.perform(get("/api/me/profile-picture"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes("image content".getBytes()));
    }

    @Test
    void testGenerateApiKey_Success() throws Exception {
        when(userService.generateApiKey(1L)).thenReturn("new-api-key");

        mockMvc.perform(post("/api/me/api-key"))
                .andExpect(status().isOk())
                .andExpect(content().string("new-api-key"));
    }

    @Test
    void testGetApiKey_Success() throws Exception {
        user.setApiKey("existing-api-key");

        mockMvc.perform(get("/api/me/api-key"))
                .andExpect(status().isOk())
                .andExpect(content().string("existing-api-key"));
    }
}
