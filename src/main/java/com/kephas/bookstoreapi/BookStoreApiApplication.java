package com.kephas.bookstoreapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
public class BookStoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApiApplication.class, args);
    }

}
