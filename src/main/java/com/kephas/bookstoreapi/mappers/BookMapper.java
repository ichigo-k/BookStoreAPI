package com.kephas.bookstoreapi.mappers;

import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.services.AuthorService;
import com.kephas.bookstoreapi.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor

public class BookMapper {

    private final CategoryService categoryService;
    private  final AuthorService authorService;


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
        Author author =  authorService.getOneAuthor(bookDto.authorId());
        Category cat = categoryService.getOneCategory(bookDto.categoryId());
        return new Book(
                bookDto.id(),
                bookDto.title(),
                bookDto.isbn(),
                bookDto.price(),
                bookDto.publicationDate(),
                bookDto.description(),
                cat,
                author
        );
    }


}
