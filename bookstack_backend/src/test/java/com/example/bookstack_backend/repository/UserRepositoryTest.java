package com.example.bookstack_backend.repository;

import com.example.bookstack_backend.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        User user = new User("findme", "findme@mail.com", "pass");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("findme");
        assertTrue(found.isPresent());
        assertEquals("findme@mail.com", found.get().getEmail());
    }

    @Test
    void testFindByApiKey() {
        User user = new User("apikeyuser", "api@mail.com", "pass");
        user.setApiKey("secret-key");
        userRepository.save(user);

        Optional<User> found = userRepository.findByApiKey("secret-key");
        assertTrue(found.isPresent());
        assertEquals("apikeyuser", found.get().getUsername());
    }

    @Test
    void testFindAllByIsBanned() {
        User user = new User("banned", "banned@mail.com", "pass");
        user.setIsBanned(true);
        userRepository.save(user);

        List<User> bannedUsers = userRepository.findAllByIsBanned();
        assertFalse(bannedUsers.isEmpty());
        assertTrue(bannedUsers.stream().anyMatch(u -> u.getUsername().equals("banned")));
    }

    @Test
    void testApproveUser() {
        User user = new User("tobeapproved", "approve@mail.com", "pass");
        user.setIsBanned(true);
        userRepository.save(user);

        userRepository.approveUser(user.getId());
        
        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertFalse(updated.getIsBanned());
    }
}
