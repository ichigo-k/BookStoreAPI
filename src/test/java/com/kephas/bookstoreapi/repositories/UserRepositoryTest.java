package com.kephas.bookstoreapi.repositories;

import com.kephas.bookstoreapi.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");

        userRepository.save(user1);
    }

    @Test
    @DisplayName("existsByEmail should return true if user exists")
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        boolean exists = userRepository.existsByEmail("john@example.com");
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByEmail should return false if user does not exist")
    void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        boolean exists = userRepository.existsByEmail("jane@example.com");
        assertFalse(exists);
    }

    @Test
    @DisplayName("findByEmail should return a user if exists")
    void findByEmail_ShouldReturnUser_WhenExists() {
        Optional<User> found = userRepository.findByEmail("john@example.com");

        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals("john@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("findByEmail should return empty if user does not exist")
    void findByEmail_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> found = userRepository.findByEmail("jane@example.com");

        assertTrue(found.isEmpty());
    }
}
