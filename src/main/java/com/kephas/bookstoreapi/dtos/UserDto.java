package com.kephas.bookstoreapi.dtos;

import com.kephas.bookstoreapi.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserDto(
        UUID id,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @Email(message = "Please provide valid email")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min=8, message = "Password must be at least 8 characters long")
        String password,

        UserRole role
) { }
