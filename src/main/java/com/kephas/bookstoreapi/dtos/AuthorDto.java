package com.kephas.bookstoreapi.dtos;


import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

import static com.kephas.bookstoreapi.constants.ValidationMessages.*;

public record AuthorDto(
        UUID id,

        @NotBlank(message = AUTHOR_NAME_NOT_BLANK)
        String name,

        String biography,

        List<BookDto> books
) {}
