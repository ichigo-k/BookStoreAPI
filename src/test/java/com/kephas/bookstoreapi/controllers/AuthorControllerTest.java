package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.AuthorMapper;
import com.kephas.bookstoreapi.services.AuthorService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private AuthorMapper authorMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AuthorDto authorDto;
    private Author author;
    private String authorJson;

    @BeforeEach
    void setup() {
        authorDto = new AuthorDto(null, "Kephas", "Test Author", null);
        author = new Author(null, "Kephas", "Test Author", null);

        authorJson = """
                {
                    "name": "Kephas",
                    "biography": "Test Author"
                }
                """;
    }

    @Nested
    @DisplayName("GET /api/v1/authors")
    class GetAuthors {

        @Test
        @DisplayName("getAllAuthors_shouldReturn200")
        void getAllAuthors_shouldReturn200() throws Exception {
            mockMvc.perform(get("/api/v1/authors"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/authors/{id}")
    class GetAuthor {

        @Test
        @DisplayName("getAuthorById_shouldReturn200")
        void getAuthorById_shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            when(authorService.getOneAuthor(id)).thenReturn(author);
            when(authorMapper.toDto(author)).thenReturn(authorDto);

            mockMvc.perform(get("/api/v1/authors/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Kephas"))
                    .andExpect(jsonPath("$.data.biography").value("Test Author"));
        }

        @Test
        @DisplayName("getAuthorById_shouldReturn404")
        void getAuthorById_shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            when(authorService.getOneAuthor(id)).thenThrow(new ResourceNotFoundException("Author not found"));

            mockMvc.perform(get("/api/v1/authors/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("getAuthorById_shouldReturn400_forInvalidUUID")
        void getAuthorById_shouldReturn400_forInvalidUUID() throws Exception {
            mockMvc.perform(get("/api/v1/authors/invalid-id"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/authors")
    class CreateAuthor {

        @Test
        @DisplayName("createAuthor_shouldReturn201")
        void createAuthor_shouldReturn201() throws Exception {
            mockMvc.perform(post("/api/v1/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authorJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Author created successfully"));
        }

        @Test
        @DisplayName("createAuthor_shouldReturn400_forInvalidData")
        void createAuthor_shouldReturn400_forInvalidData() throws Exception {
            String invalidJson = "{}";

            mockMvc.perform(post("/api/v1/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/authors/{id}")
    class UpdateAuthor {

        @Test
        @DisplayName("updateAuthor_shouldReturn200")
        void updateAuthor_shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(authorService).updateAuthor(eq(id), any(AuthorDto.class));

            mockMvc.perform(patch("/api/v1/authors/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authorJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Author updated successfully"));
        }

        @Test
        @DisplayName("updateAuthor_shouldReturn404")
        void updateAuthor_shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Author not found")).when(authorService).updateAuthor(id, authorDto);

            mockMvc.perform(patch("/api/v1/authors/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authorJson))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("updateAuthor_shouldReturn400_forInvalidData")
        void updateAuthor_shouldReturn400_forInvalidData() throws Exception {
            UUID id = UUID.randomUUID();
            String invalidJson = "{}";

            mockMvc.perform(patch("/api/v1/authors/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/authors/{id}")
    class DeleteAuthor {

        @Test
        @DisplayName("deleteAuthor_shouldReturn200")
        void deleteAuthor_shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(delete("/api/v1/authors/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Author deleted successfully"));
        }

        @Test
        @DisplayName("deleteAuthor_shouldReturn404")
        void deleteAuthor_shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Author not found")).when(authorService).deleteAuthor(id);

            mockMvc.perform(delete("/api/v1/authors/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
