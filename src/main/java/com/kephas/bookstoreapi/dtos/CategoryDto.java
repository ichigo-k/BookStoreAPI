package com.kephas.bookstoreapi.dtos;

import static com.kephas.bookstoreapi.constants.ValidationMessages.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record CategoryDto(

        UUID id,
        @NotBlank(message =CATEGORY_NAME_NOT_BLANK)
        String name,
        String description,
        List<BookDto> books
) {
}
