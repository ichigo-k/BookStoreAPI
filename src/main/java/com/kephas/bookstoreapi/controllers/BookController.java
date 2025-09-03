package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.mappers.BookMapper;
import com.kephas.bookstoreapi.services.BookService;
import com.kephas.bookstoreapi.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/books")
@AllArgsConstructor
public class BookController {
    private final BookMapper bookMapper;
    private final BookService bookService;


    @GetMapping
    public ApiResponse<Object> getBooks(){
        List<BookDto> books = bookService.getBooks()
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
        return ApiResponse.success(null, books);
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createBook(@Valid @RequestBody BookDto bookDto ){
        bookService.createBook(bookDto);
        ApiResponse<Object> response = ApiResponse.success(201,"Book created successfully", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<Object> getBook(@PathVariable UUID id){
        BookDto bookDto = bookService.getBook(id);
        return ApiResponse.success(null, bookDto);
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteBook(@PathVariable UUID id){
        bookService.deleteBook(id);
        return ApiResponse.success(null, "Book Deleted successfully");
    }

    @PatchMapping("/{id}")
    public ApiResponse<Object> updateBook(@PathVariable UUID id, @Valid @RequestBody BookDto bookDto){
        bookService.updateBook(id,bookDto );
        return ApiResponse.success(null, "Book updated successfully");
    }

    @GetMapping("/search")
    public ApiResponse<Object> searchForBooks(@RequestParam(required = false) String title, @RequestParam(required = false) String authorName, @RequestParam(required = false) String categoryName, @RequestParam(required = false) Integer year){
        List<BookDto> books = bookService.searchBooks(title, authorName, categoryName, year)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            return ApiResponse.success("No books found matching the search criteria", books);
        }

        String message = String.format("Found %d book%s matching the search criteria",
                books.size(), books.size() > 1 ? "s" : "");
        return ApiResponse.success(message, books);
    }
}
