package com.kephas.bookstoreapi.dtos;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

import static com.kephas.bookstoreapi.constants.ValidationMessages.*;

public record BookDto(
        UUID id,

        @NotBlank(message = TITLE_NOT_BLANK)
        String title,

        @NotBlank(message =ISBN_NOT_BLANK)
        @Column(unique = true)
        String isbn,

        @NotNull(message = PRICE_NOT_NULL)
        java.math.BigDecimal price,

        @NotNull(message = PUBLICATION_DATE_NOT_NULL)
        LocalDate publicationDate,

        @NotBlank(message = DESCRIPTION_NOT_BLANK)
        @Size(min=10, max = 255, message = DESCRIPTION_SIZE)
        String description,

        @NotNull(message = CATEGORY_REQUIRED)
        UUID categoryId,
        String categoryName,

        @NotNull(message = AUTHOR_REQUIRED)
        UUID authorId,
        String authorName
) {}
