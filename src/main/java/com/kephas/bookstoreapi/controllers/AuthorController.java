package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.mappers.AuthorMapper;
import com.kephas.bookstoreapi.services.AuthorService;
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
@RequestMapping("api/v1/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private AuthorMapper authorMapper;

    @GetMapping
    public ApiResponse<List<AuthorDto>> getAuthors() {
        List<AuthorDto> authors = authorService.getAuthors()
                .stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
        return ApiResponse.success(null, authors);
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createAuthor(@Valid @RequestBody AuthorDto authorDto){
        authorService.createAuthor(authorDto);

        ApiResponse<Object> response = ApiResponse.success(201,"Author created successfully", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<AuthorDto> getAuthor(@PathVariable UUID id){
        Author author = authorService.getOneAuthor(id);
        AuthorDto authorDto = authorMapper.toDto(author);
        return ApiResponse.success(null, authorDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<AuthorDto> deleteAuthor(@PathVariable UUID id){
        authorService.deleteAuthor(id);
        return ApiResponse.success("Author deleted successfully", null);
    }

    @PatchMapping("/{id}")
    public ApiResponse<AuthorDto> updateAuthor(@PathVariable UUID id, @Valid @RequestBody AuthorDto data){
        authorService.updateAuthor(id, data);
        return ApiResponse.success("Author updated successfully", null);
    }
}
