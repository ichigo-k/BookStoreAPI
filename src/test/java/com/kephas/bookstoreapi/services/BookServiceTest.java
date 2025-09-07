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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService unit tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    private Author author;
    private Category category;
    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setup() {
        author = new Author("Test Author", "Bio");
        category = new Category("Fiction", "Desc");

        book = new Book(
                UUID.randomUUID(),
                "Test Book",
                "1234567890",
                BigDecimal.valueOf(10.99),
                LocalDate.of(2020, 1, 1),
                "Some description",
                category,
                author
        );

        bookDto = new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                book.getPublicationDate(),
                book.getDescription(),
                category.getId(),
                category.getName(),
                author.getId(),
                author.getName()
        );
    }

    @Nested
    @DisplayName("getBooks() test")
    class GetBooksTest {
        @Test
        @DisplayName("Should return all books")
        void getBooks_ShouldReturnList() {
            when(bookRepository.findAll()).thenReturn(List.of(book));

            List<Book> result = bookService.getBooks();

            assertEquals(1, result.size());
            assertEquals("Test Book", result.get(0).getTitle());
            verify(bookRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("createBook() test")
    class CreateBookTest {
        @Test
        @DisplayName("Should create book successfully")
        void createBook_ShouldSave_WhenValid() {
            when(bookRepository.existsBookByIsbn(bookDto.isbn())).thenReturn(false);
            when(authorRepository.findById(bookDto.authorId())).thenReturn(Optional.of(author));
            when(categoryRepository.findById(bookDto.categoryId())).thenReturn(Optional.of(category));
            when(bookMapper.fromDto(bookDto)).thenReturn(book);

            bookService.createBook(bookDto);

            assertEquals(author, book.getAuthor());
            assertEquals(category, book.getCategory());
            verify(bookRepository, times(1)).save(book);
        }

        @Test
        @DisplayName("Should throw when ISBN already exists")
        void createBook_ShouldThrow_WhenIsbnExists() {
            when(bookRepository.existsBookByIsbn(bookDto.isbn())).thenReturn(true);

            assertThrows(UniqueConstraintViolationException.class, () -> bookService.createBook(bookDto));
            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when author not found")
        void createBook_ShouldThrow_WhenAuthorMissing() {
            when(bookRepository.existsBookByIsbn(bookDto.isbn())).thenReturn(false);
            when(authorRepository.findById(bookDto.authorId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(bookDto));
            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when category not found")
        void createBook_ShouldThrow_WhenCategoryMissing() {
            when(bookRepository.existsBookByIsbn(bookDto.isbn())).thenReturn(false);
            when(authorRepository.findById(bookDto.authorId())).thenReturn(Optional.of(author));
            when(categoryRepository.findById(bookDto.categoryId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(bookDto));
            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getBook() test")
    class GetBookTest {
        @Test
        @DisplayName("Should return book DTO")
        void getBook_ShouldReturn_WhenExists() {
            UUID id = book.getId();
            when(bookRepository.findById(id)).thenReturn(Optional.of(book));
            when(bookMapper.toDto(book)).thenReturn(bookDto);

            BookDto result = bookService.getBook(id);

            assertEquals("Test Book", result.title());
            verify(bookRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Should throw when book not found")
        void getBook_ShouldThrow_WhenNotFound() {
            UUID id = UUID.randomUUID();
            when(bookRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.getBook(id));
            verify(bookRepository, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("deleteBook() test")
    class DeleteBookTest {
        @Test
        @DisplayName("Should delete book by id")
        void deleteBook_ShouldDelete() {
            UUID id = book.getId();

            bookService.deleteBook(id);

            verify(bookRepository, times(1)).deleteById(id);
        }
    }

    @Nested
    @DisplayName("updateBook() test")
    class UpdateBookTest {
        @Test
        @DisplayName("Should update book successfully")
        void updateBook_ShouldUpdate_WhenValid() {
            UUID id = book.getId();

            BookDto updatedDto = new BookDto(
                    id,
                    "Updated Title",
                    "0987654321",
                    BigDecimal.valueOf(20.99),
                    LocalDate.of(2022, 5, 5),
                    "Updated desc",
                    category.getId(),
                    category.getName(),
                    author.getId(),
                    author.getName()
            );

            when(bookRepository.findById(id)).thenReturn(Optional.of(book));
            when(authorRepository.findById(updatedDto.authorId())).thenReturn(Optional.of(author));
            when(categoryRepository.findById(updatedDto.categoryId())).thenReturn(Optional.of(category));

            bookService.updateBook(id, updatedDto);

            assertEquals("Updated Title", book.getTitle());
            assertEquals("0987654321", book.getIsbn());
            assertEquals("Updated desc", book.getDescription());
            verify(bookRepository, times(1)).save(book);
        }

        @Test
        @DisplayName("Should throw when book not found")
        void updateBook_ShouldThrow_WhenBookMissing() {
            UUID id = UUID.randomUUID();
            when(bookRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(id, bookDto));
        }

        @Test
        @DisplayName("Should throw when author not found")
        void updateBook_ShouldThrow_WhenAuthorMissing() {
            UUID id = book.getId();
            when(bookRepository.findById(id)).thenReturn(Optional.of(book));
            when(authorRepository.findById(bookDto.authorId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(id, bookDto));
        }

        @Test
        @DisplayName("Should throw when category not found")
        void updateBook_ShouldThrow_WhenCategoryMissing() {
            UUID id = book.getId();
            when(bookRepository.findById(id)).thenReturn(Optional.of(book));
            when(authorRepository.findById(bookDto.authorId())).thenReturn(Optional.of(author));
            when(categoryRepository.findById(bookDto.categoryId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(id, bookDto));
        }
    }

    @Nested
    @DisplayName("searchBooks() test")
    class SearchBooksTest {
        @Test
        @DisplayName("Should return all books when no filters provided")
        void searchBooks_ShouldReturnAll_WhenNoFilters() {
            when(bookRepository.findAll()).thenReturn(List.of(book));

            List<Book> result = bookService.searchBooks(null, null, null, null);

            assertEquals(1, result.size());
            verify(bookRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should apply filters and return matching books")
        void searchBooks_ShouldReturnFiltered_WhenFiltersProvided() {
            when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of(book));

            List<Book> result = bookService.searchBooks("Test", "Test Author", "Fiction", 2020);

            assertEquals(1, result.size());
            verify(bookRepository, times(1)).findAll(any(Specification.class));
        }
    }
}
