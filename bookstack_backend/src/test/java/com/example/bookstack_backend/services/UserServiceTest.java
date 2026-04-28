package com.example.bookstack_backend.services;

import com.example.bookstack_backend.models.ERole;
import com.example.bookstack_backend.models.Role;
import com.example.bookstack_backend.models.User;
import com.example.bookstack_backend.repository.RoleRepository;
import com.example.bookstack_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void registerUser_ShouldRegisterWithDefaultRole_WhenNoRolesProvided() {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        Role userRole = new Role(ERole.ROLE_USER);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser("testuser", "test@example.com", "password", null);

        assertNotNull(registeredUser);
        assertEquals(1, registeredUser.getRoles().size());
        assertTrue(registeredUser.getRoles().contains(userRole));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldRegisterWithSpecifiedRoles() {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        Role adminRole = new Role(ERole.ROLE_ADMIN);
        Role userRole = new Role(ERole.ROLE_USER);
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        Set<String> strRoles = new HashSet<>();
        strRoles.add("admin");
        strRoles.add("user");

        User registeredUser = userService.registerUser("testuser", "test@example.com", "password", strRoles);

        assertNotNull(registeredUser);
        assertEquals(2, registeredUser.getRoles().size());
        assertTrue(registeredUser.getRoles().contains(adminRole));
        assertTrue(registeredUser.getRoles().contains(userRole));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldRegisterWithDefaultRole_WhenOtherRoleProvided() {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        Role userRole = new Role(ERole.ROLE_USER);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        Set<String> strRoles = new HashSet<>();
        strRoles.add("other");

        User registeredUser = userService.registerUser("testuser", "test@example.com", "password", strRoles);

        assertNotNull(registeredUser);
        assertEquals(1, registeredUser.getRoles().size());
        assertTrue(registeredUser.getRoles().contains(userRole));
    }

    @Test
    void registerUser_ShouldThrowException_WhenRoleNotFound() {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.registerUser("testuser", "test@example.com", "password", null));
    }

    @Test
    void registerUser_ShouldEncryptPassword() {
        // Tests REQ-5: The system shall use encryption for password handling
        String rawPassword = "password123";
        String encodedPassword = "encoded_password_abc";
        
        when(encoder.encode(rawPassword)).thenReturn(encodedPassword);
        
        Role userRole = new Role(ERole.ROLE_USER);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        
        User registeredUser = userService.registerUser("testuser", "test@example.com", rawPassword, null);
        
        assertEquals(encodedPassword, registeredUser.getPassword());
        verify(encoder).encode(rawPassword);
    }

    @Test
    void updateProfilePicture_ShouldUpdateUser() {
        byte[] image = "image".getBytes();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateProfilePicture(1L, image, "image/jpeg");

        assertArrayEquals(image, user.getProfilePicture());
        assertEquals("image/jpeg", user.getProfilePictureType());
        verify(userRepository).save(user);
    }

    @Test
    void hasProfilePicture_ShouldReturnTrue_WhenExists() {
        user.setProfilePicture("exists".getBytes());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertTrue(userService.hasProfilePicture(1L));
    }

    @Test
    void generateApiKey_ShouldGenerateAndSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String key = userService.generateApiKey(1L);

        assertNotNull(key);
        assertEquals(key, user.getApiKey());
        verify(userRepository).save(user);
    }

    @Test
    void findByApiKey_ShouldReturnUser() {
        when(userRepository.findByApiKey("key")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByApiKey("key");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testuser");

        assertEquals(user, result);
    }
}
