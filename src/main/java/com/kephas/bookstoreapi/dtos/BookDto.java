package com.kephas.bookstoreapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record BookDto(
        UUID id,

        @NotBlank(message = "Book title cannot be blank")
        String title,

        @NotBlank(message = "Book isbn cannot be blank")
        String isbn,

        java.math.BigDecimal price,

        @NotBlank(message = "Publication date cannot be blank")
        LocalDate publicationDate,

        @NotBlank(message = "Book description cannot be blank")
        @Size(min=10, max = 255, message = "Description must be 10 to 255 characters long ")
        String description,

        @NotBlank(message = "Category is required")
        UUID categoryId,
        String categoryName,

        @NotBlank(message = "Author is required")
        UUID authorId,
        String authorName
) {}
