package com.kephas.bookstoreapi.dtos;

import com.kephas.bookstoreapi.enums.UserRole;
import java.util.UUID;

public record UserDtoResponse(
        UUID id,
        String name,
        String email,
        UserRole role

) { }
