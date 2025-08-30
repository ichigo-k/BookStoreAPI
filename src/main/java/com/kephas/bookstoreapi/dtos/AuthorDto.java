package com.kephas.bookstoreapi.dtos;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record AuthorDto(
        UUID id,

        @NotBlank(message = "Author name cannot be blank")
        String name,

        String biography,

        List<BookDto> books
) {}
