package com.kephas.bookstoreapi.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record CategoryDto(

        UUID id,
        @NotBlank(message = "Category name cannot be blank")
        String name,
        String description,
        List<BookDto> books
) {
}
