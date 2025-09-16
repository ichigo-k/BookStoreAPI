package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.exceptions.UniqueConstraintViolationException;
import com.kephas.bookstoreapi.mappers.BookMapper;
import com.kephas.bookstoreapi.repositories.AuthorRepository;
import com.kephas.bookstoreapi.repositories.BookRepository;
import com.kephas.bookstoreapi.repositories.CategoryRepository;
import com.kephas.bookstoreapi.specifications.BookSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.kephas.bookstoreapi.constants.BookMessages.*;

@AllArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;


    public List<BookDto> getBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createBook(@Valid BookDto bookDto) {

        if (bookRepository.existsBookByIsbn(bookDto.isbn())){
            throw new UniqueConstraintViolationException(BOOK_EXISTS_BY_ISBN);
        }

        Author author = authorRepository.findById(bookDto.authorId()).orElseThrow( ()-> new ResourceNotFoundException(AUTHOR_NOT_FOUND));
        Category category = categoryRepository.findById(bookDto.categoryId()).orElseThrow( ()-> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        Book book = bookMapper.fromDto(bookDto);
        book.setAuthor(author);
        book.setCategory(category);
        bookRepository.save(book);
    }


    public BookDto getBook(UUID id) {
        Book book = bookRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException(String.format(BOOK_NOT_FOUND_BY_ID, id)));
        return bookMapper.toDto(book);
    }

    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }


    @Transactional
    public void updateBook(UUID id, @Valid BookDto bookDto) {
        Book book = bookRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException(String.format(BOOK_NOT_FOUND_BY_ID, id)));
        Author author = authorRepository.findById(bookDto.authorId()).orElseThrow( ()-> new ResourceNotFoundException(AUTHOR_NOT_FOUND));
        Category category = categoryRepository.findById(bookDto.categoryId()).orElseThrow( ()-> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        book.setTitle(bookDto.title());
        book.setIsbn(bookDto.isbn());
        book.setDescription(bookDto.description());
        book.setPrice(bookDto.price());
        book.setPublicationDate(bookDto.publicationDate());
        book.setAuthor(author);
        book.setCategory(category);
        bookRepository.save(book);
    }


    public List<BookDto> searchBooks(String title, String authorName, String categoryName, Integer year) {

        if (title == null && authorName == null && categoryName ==null && year == null){
            return bookRepository.findAll().stream()
                    .map(bookMapper::toDto)
                    .collect(Collectors.toList());
        }

        Specification<Book> spec = Specification.allOf(
                BookSpecification.hasTitle(title),
                BookSpecification.hasAuthor(authorName),
                BookSpecification.hasCategory(categoryName),
                BookSpecification.publishedInYear(year)
        );


        return bookRepository.findAll(spec) .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());

    }

}
