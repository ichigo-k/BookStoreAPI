package com.kephas.bookstoreapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.BookMapper;
import com.kephas.bookstoreapi.services.BookService;
import com.kephas.bookstoreapi.utils.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookMapper bookMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private BookDto bookDto;
    private Book book;

    @BeforeEach
    void setup() {
        Category category = new Category(UUID.randomUUID(), "Fiction", null, null);
        Author author = new Author(UUID.randomUUID(), "John Doe", null, null);

        bookDto = new BookDto(
                null, "Test Book", "isb59595959595", new BigDecimal("40.00"),
                LocalDate.now(), "This is a test book",
                category.getId(), category.getName(),
                author.getId(), author.getName()
        );

        book = new Book(
                null, "Test Book", "isb59595959595", new BigDecimal("40.00"),
                LocalDate.now(), "This is a test book",
                category, author
        );
    }

    @Nested
    @DisplayName("GET /api/v1/books")
    class GetBooks {
        @Test
        @DisplayName("Should return 200 OK with book list")
        void shouldReturn200() throws Exception {
            when(bookService.getBooks()).thenReturn(List.of(book));
            when(bookMapper.toDto(any())).thenReturn(bookDto);

            mockMvc.perform(get("/api/v1/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/books/{id}")
    class GetBook {
        @Test
        @DisplayName("Should return 200 OK for valid ID")
        void shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            when(bookService.getBook(id)).thenReturn(bookDto);

            mockMvc.perform(get("/api/v1/books/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.title").value("Test Book"));
        }

        @Test
        @DisplayName("Should return 404 NotFound for missing book")
        void shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            when(bookService.getBook(id)).thenThrow(new ResourceNotFoundException("Book not found"));

            mockMvc.perform(get("/api/v1/books/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should return 400 BadRequest for invalid UUID")
        void shouldReturn400() throws Exception {
            mockMvc.perform(get("/api/v1/books/123"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/books")
    class CreateBook {
        @Test
        @DisplayName("Should return 201 Created for valid book")
        void shouldReturn201() throws Exception {
            String json = objectMapper.writeValueAsString(bookDto);

            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Book created successfully"));
        }

        @Test
        @DisplayName("Should return 400 BadRequest for invalid data")
        void shouldReturn400() throws Exception {
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/books/{id}")
    class UpdateBook {
        @Test
        @DisplayName("Should return 200 OK when book updated")
        void shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            String json = objectMapper.writeValueAsString(bookDto);
            doNothing().when(bookService).updateBook(eq(id), any(BookDto.class));

            mockMvc.perform(patch("/api/v1/books/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Book updated successfully"));

            verify(bookService).updateBook(eq(id), any(BookDto.class));
        }

        @Test
        @DisplayName("Should return 404 NotFound when book missing")
        void shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            String json = objectMapper.writeValueAsString(bookDto);
            doThrow(new ResourceNotFoundException("Book not found"))
                    .when(bookService).updateBook(eq(id), any(BookDto.class));

            mockMvc.perform(patch("/api/v1/books/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/books/{id}")
    class DeleteBook {
        @Test
        @DisplayName("Should return 200 OK when book deleted")
        void shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(bookService).deleteBook(id);

            mockMvc.perform(delete("/api/v1/books/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Book deleted successfully"));
        }

        @Test
        @DisplayName("Should return 404 NotFound when book missing")
        void shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Book not found"))
                    .when(bookService).deleteBook(id);

            mockMvc.perform(delete("/api/v1/books/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/books/search")
    class SearchBooks {
        @Test
        @DisplayName("Should return 200 OK with results")
        void shouldReturn200WithResults() throws Exception {
            when(bookService.searchBooks("Book Title", null, null, null)).thenReturn(List.of(book));
            when(bookMapper.toDto(any())).thenReturn(bookDto);

            mockMvc.perform(get("/api/v1/books/search")
                            .param("title", "Book Title"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.message").value("Found 1 book matching the search criteria"));
        }

        @Test
        @DisplayName("Should return 200 OK with no results")
        void shouldReturn200WithNoResults() throws Exception {
            when(bookService.searchBooks("Nonexistent", null, null, null)).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/books/search")
                            .param("title", "Nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.message").value("No books found matching the search criteria"));
        }
    }
}
