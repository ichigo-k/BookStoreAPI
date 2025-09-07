package com.kephas.bookstoreapi.mappers;

import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.entities.Book;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor

public class BookMapper {

    public BookDto toDto(Book book){
        return  new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                book.getPublicationDate(),
                book.getDescription(),
                book.getCategory().getId(),
                book.getCategory().getName(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }

    public Book fromDto(BookDto bookDto) {
        return new Book(
                bookDto.id(),
                bookDto.title(),
                bookDto.isbn(),
                bookDto.price(),
                bookDto.publicationDate(),
                bookDto.description(),
                null,
                null

        );
    }


}
