package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.CategoryDto;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.CategoryMapper;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService unit tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private Book book;

    @BeforeEach
    void setup() {
        category = new Category("Fiction", "Books with fictional content");
        categoryDto = new CategoryDto(null, category.getName(), category.getDescription(), null);
        book = new Book(null, "Book Title", "12345", new BigDecimal( 30), null, null, category, null);
    }

    @Nested
    @DisplayName("getCategories() test")
    class GetCategoriesTest {
        @Test
        @DisplayName("Should return list of categories")
        void getCategories_ShouldReturnList() {
            when(categoryRepository.findAll()).thenReturn(List.of(category));

            List<Category> result = categoryService.getCategories();

            assertEquals(1, result.size());
            assertEquals("Fiction", result.get(0).getName());
            verify(categoryRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getOneCategory() test")
    class GetOneCategoryTest {
        @Test
        @DisplayName("Should return single category")
        void getOneCategory_ShouldReturn_WhenExists() {
            UUID id = category.getId();
            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryMapper.toDto(category)).thenReturn(categoryDto);

            CategoryDto result = categoryService.getOneCategory(id);

            assertEquals("Fiction", result.name());
            verify(categoryRepository, times(1)).findById(id);
            verify(categoryMapper, times(1)).toDto(category);
        }

        @Test
        @DisplayName("Should throw when category not found")
        void getOneCategory_ShouldThrow_WhenNotFound() {
            UUID id = UUID.randomUUID();
            when(categoryRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.getOneCategory(id));
        }
    }

    @Nested
    @DisplayName("createCategory() test")
    class CreateCategoryTest {
        @Test
        @DisplayName("Should create new category")
        void createCategory_ShouldSave() {
            when(categoryMapper.fromDto(categoryDto)).thenReturn(category);

            categoryService.createCategory(categoryDto);

            verify(categoryMapper, times(1)).fromDto(categoryDto);
            verify(categoryRepository, times(1)).save(category);
        }
    }

    @Nested
    @DisplayName("updateCategory() test")
    class UpdateCategoryTest {
        @Test
        @DisplayName("Should update category successfully")
        void updateCategory_ShouldUpdate_WhenExists() {
            UUID id = category.getId();
            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

            CategoryDto updatedDto = new CategoryDto(id, "Updated Name", "Updated Desc", null);

            categoryService.updateCategory(id, updatedDto);

            assertEquals("Updated Name", category.getName());
            assertEquals("Updated Desc", category.getDescription());
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @DisplayName("Should throw when category not found")
        void updateCategory_ShouldThrow_WhenNotFound() {
            UUID id = UUID.randomUUID();
            when(categoryRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, categoryDto));
            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCategory() test")
    class DeleteCategoryTest {
        @Test
        @DisplayName("Should delete category with no books")
        void deleteCategory_ShouldDelete_WhenNoBooks() {
            UUID id = category.getId();
            when(categoryRepository.existsById(id)).thenReturn(true);
            when(bookRepository.countBooksByCategory_Id(id)).thenReturn(0);

            categoryService.deleteCategory(id);

            verify(categoryRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Should reassign books to Uncategorized and delete category")
        void deleteCategory_ShouldReassignBooks_WhenBooksExist() {
            UUID id = category.getId();
            when(categoryRepository.existsById(id)).thenReturn(true);
            when(bookRepository.countBooksByCategory_Id(id)).thenReturn(1);
            when(bookRepository.findBooksByCategory_Id(id)).thenReturn(List.of(book));

            Category uncategorized = new Category("Uncategorized", "Auto-created category");
            when(categoryRepository.findByName("Uncategorized")).thenReturn(Optional.of(uncategorized));

            categoryService.deleteCategory(id);

            assertEquals(uncategorized, book.getCategory());
            verify(bookRepository, times(1)).saveAll(List.of(book));
            verify(categoryRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Should create Uncategorized if not exists")
        void deleteCategory_ShouldCreateUncategorized_WhenMissing() {
            UUID id = category.getId();
            when(categoryRepository.existsById(id)).thenReturn(true);
            when(bookRepository.countBooksByCategory_Id(id)).thenReturn(1);
            when(bookRepository.findBooksByCategory_Id(id)).thenReturn(List.of(book));

            when(categoryRepository.findByName("Uncategorized")).thenReturn(Optional.empty());

            Category uncategorized = new Category("Uncategorized", "Auto-created category");
            when(categoryRepository.save(any(Category.class))).thenReturn(uncategorized);

            categoryService.deleteCategory(id);

            assertEquals("Uncategorized", book.getCategory().getName());
            verify(bookRepository, times(1)).saveAll(List.of(book));
            verify(categoryRepository, times(1)).save(any(Category.class));
            verify(categoryRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Should throw when category not found")
        void deleteCategory_ShouldThrow_WhenNotFound() {
            UUID id = UUID.randomUUID();
            when(categoryRepository.existsById(id)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(id));
            verify(categoryRepository, never()).deleteById(id);
        }
    }
}
