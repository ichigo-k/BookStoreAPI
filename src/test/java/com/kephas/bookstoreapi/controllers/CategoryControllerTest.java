package com.kephas.bookstoreapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kephas.bookstoreapi.dtos.CategoryDto;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.CategoryMapper;
import com.kephas.bookstoreapi.services.CategoryService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CategoryDto categoryDto;
    private Category category;

    @BeforeEach
    void setup() {
        categoryDto = new CategoryDto(null, "Fiction", "Fiction books", null);
        category = new Category(null, "Fiction", "Fiction books", null);
    }

    @Nested
    @DisplayName("GET /api/v1/categories")
    class GetCategories {
        @Test
        @DisplayName("Should return 200 OK with category list")
        void shouldReturn200() throws Exception {
            when(categoryService.getCategories()).thenReturn(List.of(category));
            when(categoryMapper.toDto(any())).thenReturn(categoryDto);

            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/{id}")
    class GetCategory {
        @Test
        @DisplayName("Should return 200 OK for valid ID")
        void shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            when(categoryService.getOneCategory(id)).thenReturn(categoryDto);

            mockMvc.perform(get("/api/v1/categories/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Fiction"));
        }

        @Test
        @DisplayName("Should return 404 NotFound for missing category")
        void shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            when(categoryService.getOneCategory(id)).thenThrow(new ResourceNotFoundException("Category not found"));

            mockMvc.perform(get("/api/v1/categories/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should return 400 BadRequest for invalid UUID")
        void shouldReturn400() throws Exception {
            mockMvc.perform(get("/api/v1/categories/123"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/categories")
    class CreateCategory {
        @Test
        @DisplayName("Should return 201 Created for valid category")
        void shouldReturn201() throws Exception {
            String json = objectMapper.writeValueAsString(categoryDto);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Category created successfully"));
        }

        @Test
        @DisplayName("Should return 400 BadRequest for invalid data")
        void shouldReturn400() throws Exception {
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/categories/{id}")
    class UpdateCategory {
        @Test
        @DisplayName("Should return 200 OK when category updated")
        void shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            String json = objectMapper.writeValueAsString(categoryDto);
            doNothing().when(categoryService).updateCategory(eq(id), any(CategoryDto.class));

            mockMvc.perform(patch("/api/v1/categories/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Category updated successfully"));

            verify(categoryService).updateCategory(eq(id), any(CategoryDto.class));
        }

        @Test
        @DisplayName("Should return 404 NotFound when category missing")
        void shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            String json = objectMapper.writeValueAsString(categoryDto);
            doThrow(new ResourceNotFoundException("Category not found"))
                    .when(categoryService).updateCategory(eq(id), any(CategoryDto.class));

            mockMvc.perform(patch("/api/v1/categories/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/categories/{id}")
    class DeleteCategory {
        @Test
        @DisplayName("Should return 200 OK when category deleted")
        void shouldReturn200() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(categoryService).deleteCategory(id);

            mockMvc.perform(delete("/api/v1/categories/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Category deleted successfully"));
        }

        @Test
        @DisplayName("Should return 404 NotFound when category missing")
        void shouldReturn404() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Category not found"))
                    .when(categoryService).deleteCategory(id);

            mockMvc.perform(delete("/api/v1/categories/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
