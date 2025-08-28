package com.kephas.bookstoreapi.dtos;

import com.kephas.bookstoreapi.entities.UserRole;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email,
        String password,
        UserRole role
) { }
