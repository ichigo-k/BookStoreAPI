package com.kephas.bookstoreapi.dtos;

import java.util.List;
import java.util.UUID;

public record CategoryDto(

        UUID id,
        String name,
        String description,
        List<BookDto> books
) {
}
