package com.kephas.bookstoreapi.mappers;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AuthorMapper {
    private BookMapper bookMapper;


    public AuthorDto toDto(Author author){

        List<BookDto> books = author.getBooks() == null ? List.of() : author.getBooks().stream().map(bookMapper::toDto).toList() ;
        return new AuthorDto(
                author.getId(),
                author.getName(),
                author.getBiography(),
                books
        );
    }

    public Author fromDto(AuthorDto authorDto){

        List<Book> books = authorDto.books() == null ? List.of() : authorDto.books().stream().map(bookMapper::fromDto).toList();

        return new Author(authorDto.id(),
                authorDto.name(),
                authorDto.biography(),
               books
        );
    }
}
