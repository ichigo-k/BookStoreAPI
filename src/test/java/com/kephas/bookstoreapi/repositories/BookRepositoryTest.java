package com.kephas.bookstoreapi.repositories;

import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("BookRepository Custom Methods Tests")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Author author1;
    private Author author2;
    private Category category1;
    private Category category2;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setup() {
        author1 = authorRepository.save(new Author("Author One", "Bio One"));
        author2 = authorRepository.save(new Author("Author Two", "Bio Two"));

        category1 = categoryRepository.save(new Category("Category One", "Desc One"));
        category2 = categoryRepository.save(new Category("Category Two", "Desc Two"));


        book1 = bookRepository.save(new Book(
                null,
                "Book One",
                "ISBN1",
                new BigDecimal("10.0"),
                LocalDate.of(2023, 1, 1),
                "Desc1",
                category1,
                author1
        ));

        book2 = bookRepository.save(new Book(
                null,
                "Book Two",
                "ISBN2",
                new BigDecimal("20.0"),
                LocalDate.of(2024, 1, 1),
                "Desc2",
                category2,
                author1
        ));

    }

    @Test
    @DisplayName("findBooksByAuthor_Id should return books of a given author")
    void findBooksByAuthorId_ShouldReturnBooks() {
        List<Book> books = bookRepository.findBooksByAuthor_Id(author1.getId());
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Book One")));
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Book Two")));
    }

    @Test
    @DisplayName("countBooksByAuthor_Id should return correct count")
    void countBooksByAuthorId_ShouldReturnCount() {
        int count = bookRepository.countBooksByAuthor_Id(author1.getId());
        assertEquals(2, count);

        count = bookRepository.countBooksByAuthor_Id(author2.getId());
        assertEquals(0, count);
    }

    @Test
    @DisplayName("findBooksByCategory_Id should return books of a given category")
    void findBooksByCategoryId_ShouldReturnBooks() {
        List<Book> books = bookRepository.findBooksByCategory_Id(category1.getId());
        assertEquals(1, books.size());
        assertEquals("Book One", books.get(0).getTitle());
    }

    @Test
    @DisplayName("countBooksByCategory_Id should return correct count")
    void countBooksByCategoryId_ShouldReturnCount() {
        int count = bookRepository.countBooksByCategory_Id(category1.getId());
        assertEquals(1, count);

        count = bookRepository.countBooksByCategory_Id(category2.getId());
        assertEquals(1, count);
    }

    @Test
    @DisplayName("existsBookByIsbn should return true if ISBN exists")
    void existsBookByIsbn_ShouldReturnTrue_WhenExists() {
        assertTrue(bookRepository.existsBookByIsbn("ISBN1"));
        assertTrue(bookRepository.existsBookByIsbn("ISBN2"));
    }

    @Test
    @DisplayName("existsBookByIsbn should return false if ISBN does not exist")
    void existsBookByIsbn_ShouldReturnFalse_WhenNotExists() {
        assertFalse(bookRepository.existsBookByIsbn("NON_EXISTENT_ISBN"));
    }
}
