package com.kephas.bookstoreapi.utils;

import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.entities.UserRole;
import com.kephas.bookstoreapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (userRepository.findByEmail("admin@mail.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@mail.com");
                admin.setRole(UserRole.ADMIN);
                admin.setPassword(passwordEncoder.encode("password"));
                userRepository.save(admin);
                System.out.println("Admin user created!");
            } else {
                System.out.println("Admin user already exists, skipping seeding.");
            }
        };
    }
}
