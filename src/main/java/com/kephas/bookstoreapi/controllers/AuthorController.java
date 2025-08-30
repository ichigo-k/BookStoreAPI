package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.mappers.AuthorMapper;
import com.kephas.bookstoreapi.services.AuthorService;
import com.kephas.bookstoreapi.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    public ApiResponse<Object> createAuthor(@Valid @RequestBody AuthorDto authorDto){
        authorService.createAuthor(authorDto);
        return ApiResponse.success(202,"Author created successfully", null);

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
