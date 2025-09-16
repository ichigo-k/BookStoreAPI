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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;


    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public void createBook(@Valid BookDto bookDto) {

        if (bookRepository.existsBookByIsbn(bookDto.isbn())){
            throw new UniqueConstraintViolationException("Book with isbn already exists");
        }

        Author author = authorRepository.findById(bookDto.authorId()).orElseThrow( ()-> new ResourceNotFoundException("Author does not exist"));
        Category category = categoryRepository.findById(bookDto.categoryId()).orElseThrow( ()-> new ResourceNotFoundException("Category does not exist"));

        Book book = bookMapper.fromDto(bookDto);
        book.setAuthor(author);
        book.setCategory(category);
        bookRepository.save(book);
    }


    public BookDto getBook(UUID id) {
        Book book = bookRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException("Book by id: " + id+ " does not exist"));
        return bookMapper.toDto(book);
    }

    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }


    @Transactional
    public void updateBook(UUID id, @Valid BookDto bookDto) {
        Book book = bookRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException("Book by id: " + id+ " does not exist"));
        Author author = authorRepository.findById(bookDto.authorId()).orElseThrow( ()-> new ResourceNotFoundException("Author does not exist"));
        Category category = categoryRepository.findById(bookDto.categoryId()).orElseThrow( ()-> new ResourceNotFoundException("Category does not exist"));

        book.setTitle(bookDto.title());
        book.setIsbn(bookDto.isbn());
        book.setDescription(bookDto.description());
        book.setPrice(bookDto.price());
        book.setPublicationDate(bookDto.publicationDate());
        book.setAuthor(author);
        book.setCategory(category);
        bookRepository.save(book);
    }


    public List<Book> searchBooks(String title, String authorName, String categoryName, Integer year) {

        if (title == null && authorName == null && categoryName ==null && year == null){
            return bookRepository.findAll();
        }

        Specification<Book> spec = Specification.allOf(
                BookSpecification.hasTitle(title),
                BookSpecification.hasAuthor(authorName),
                BookSpecification.hasCategory(categoryName),
                BookSpecification.publishedInYear(year)
        );


        return bookRepository.findAll(spec);
    }

}
