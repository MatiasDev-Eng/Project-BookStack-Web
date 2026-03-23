package com.example.bookstack_backend.security.services;

import com.example.bookstack_backend.models.ERole;
import com.example.bookstack_backend.models.Role;
import com.example.bookstack_backend.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDetailsImplTest {

    @Test
    void build_ShouldCreateUserDetailsFromUser() {
        User user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
        Role role = new Role(ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        assertEquals(1L, userDetails.getId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void getters_ShouldReturnValues() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "email", "pass", Collections.emptyList());

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        assertEquals("email", userDetails.getEmail());
    }

    @Test
    void equals_ShouldCompareIds() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "email1", "pass1", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(1L, "user2", "email2", "pass2", Collections.emptyList());
        UserDetailsImpl user3 = new UserDetailsImpl(2L, "user3", "email3", "pass3", Collections.emptyList());

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "string");
        assertEquals(user1, user1);
    }
}
