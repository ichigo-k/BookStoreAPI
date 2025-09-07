package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.AuthorMapper;
import com.kephas.bookstoreapi.repositories.AuthorRepository;
import com.kephas.bookstoreapi.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Author service unit tests")
class AuthorServiceTest {

    private  List<Author> mockAuthors;
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorService authorService;


    @BeforeEach
    void setup(){
        mockAuthors = List.of(new Author("Kephas", ""), new Author("James", ""));
    }


    @Nested
    @DisplayName("getAuthors() test")
    class GetAuthorTest{

        @Test
        @DisplayName("Should return a list of authors")
        void getAuthors_ShouldReturnList_WhenAuthorsExist(){
            when(authorRepository.findAll()).thenReturn(mockAuthors);
            List<Author> authors = authorService.getAuthors();
            assertEquals(2, authors.size());
            verify(authorRepository, times(1)).findAll();
        }
    }



    @Nested
    @DisplayName("getOneAuthor() test")
    class GetOneAuthorTest{

        @Test
        @DisplayName("Should return a single author")
        void getAuthors_ShouldReturnAuthor_WhenAuthorExist(){
            Author firstAuthor = mockAuthors.get(0);
            when(authorRepository.findById(firstAuthor.getId())).thenReturn(Optional.of(firstAuthor));
            Author author = authorService.getOneAuthor(firstAuthor.getId());
            assertEquals("Kephas", author.getName());
            verify(authorRepository, times(1)).findById(firstAuthor.getId());
        }


        @Test
        @DisplayName("Should throw error when author does not exist")
        void getAuthors_ShouldThrowError_WhenAuthorDoesNotExist(){
            UUID mockUUID = UUID.randomUUID();
            when(authorRepository.findById(mockUUID)).thenReturn(Optional.empty());
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()-> authorService.getOneAuthor(mockUUID));
            assertEquals("Author with id: "+  mockUUID+ " does not exist", ex.getMessage());
            verify(authorRepository, times(1)).findById(mockUUID);
        }
    }



    @Nested
    @DisplayName("createAuthor() test")
    class CreateAuthorTest {

        @Test
        @DisplayName("Should create a new author")
        void createAuthor_ShouldSave_WhenDtoValid() {
            AuthorDto dto = new AuthorDto(null, "Test", "This is a test", null);
            Author mappedAuthor = new Author(dto.name(), dto.biography());

            when(authorMapper.fromDto(dto)).thenReturn(mappedAuthor);

            authorService.createAuthor(dto);

            verify(authorMapper, times(1)).fromDto(dto);
            verify(authorRepository, times(1)).save(mappedAuthor);
        }
    }



    @Nested
    @DisplayName("updateAuthor() test")
    class UpdateAuthorTest {

        @Test
        @DisplayName("Should update an existing author")
        void updateAuthor_ShouldUpdate_WhenAuthorExists() {
            Author existing = mockAuthors.get(1);
            UUID id = existing.getId();
            AuthorDto updateDto = new AuthorDto(null, "Updated Name", "Updated bio", null);

            when(authorRepository.findById(id)).thenReturn(Optional.of(existing));

            authorService.updateAuthor(id, updateDto);

            assertEquals("Updated Name", existing.getName());
            assertEquals("Updated bio", existing.getBiography());
            verify(authorRepository, times(1)).save(existing);
        }


        @Test
        @DisplayName("Should throw error when updating non-existing author")
        void updateAuthor_ShouldThrowError_WhenAuthorDoesNotExist() {
            UUID mockUUID = UUID.randomUUID();
            AuthorDto updateDto = new AuthorDto(null, "Updated Name", "Updated bio", null);

            when(authorRepository.findById(mockUUID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> authorService.updateAuthor(mockUUID, updateDto));
            verify(authorRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("deleteAuthor() test")
    class DeleteAuthorTest {

        @Test
        @DisplayName("Should delete author when no books assigned")
        void deleteAuthor_ShouldDelete_WhenNoBooksAssigned() {
            Author author = mockAuthors.get(1);
            UUID id = author.getId();

            when(authorRepository.existsById(id)).thenReturn(true);
            when(bookRepository.countBooksByAuthor_Id(id)).thenReturn(0);

            authorService.deleteAuthor(id);

            verify(authorRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Should reassign books to Unknown and delete author")
        void deleteAuthor_ShouldReassignBooks_WhenBooksAssigned() {
            Author author = mockAuthors.get(1);
            UUID id = author.getId();
            Book book = new Book(UUID.randomUUID(), "Test Book", "3444444eroerg", new BigDecimal(45.00), LocalDate.now(), "This is a test", null, null);

            List<Book> books = List.of(book);

            when(authorRepository.existsById(id)).thenReturn(true);
            when(bookRepository.countBooksByAuthor_Id(id)).thenReturn(1);
            when(bookRepository.findBooksByAuthor_Id(id)).thenReturn(books);

            Author unknown = new Author("Unknown", "Author information is currently unavailable...");
            when(authorRepository.findAuthorByName("Unknown")).thenReturn(Optional.of(unknown));

            authorService.deleteAuthor(id);

            assertEquals(unknown, book.getAuthor());
            verify(bookRepository, times(1)).saveAll(books);
            verify(authorRepository, times(1)).deleteById(id);
        }


        @Test
        @DisplayName("Should throw error when author does not exist")
        void deleteAuthor_ShouldThrowError_WhenAuthorDoesNotExist() {
            UUID mockUUID = UUID.randomUUID();

            when(authorRepository.existsById(mockUUID)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthor(mockUUID));
            verify(authorRepository, never()).deleteById(mockUUID);
        }
    }




}