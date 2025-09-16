package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.CategoryDto;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.CategoryMapper;
import com.kephas.bookstoreapi.repositories.BookRepository;
import com.kephas.bookstoreapi.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.kephas.bookstoreapi.constants.CategoryMessages.*;


@AllArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;

    public CategoryDto getOneCategory(UUID id){
        Category category = categoryRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND_BY_ID, id)));
        return categoryMapper.toDto(category);
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public void createCategory(CategoryDto categoryDto) {
        Category category =  categoryMapper.fromDto(categoryDto);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(CATEGORY_NOT_FOUND);
        }

        if (bookRepository.countBooksByCategory_Id(id) >= 1) {
            List<Book> books = bookRepository.findBooksByCategory_Id(id);

            Category uncategorized = categoryRepository.findByName(UNCATEGORIZED_NAME)
                    .orElseGet(() -> {
                        Category newCategory = new Category(
                                UNCATEGORIZED_NAME,
                                UNCATEGORIZED_DESCRIPTION
                        );
                        return categoryRepository.save(newCategory);
                    });
            books.forEach(book -> book.setCategory(uncategorized));
            bookRepository.saveAll(books);
        }

        categoryRepository.deleteById(id);
    }


    public void updateCategory(UUID id, CategoryDto data) {
        Category category = categoryRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND_BY_ID, id)));
        category.setName(data.name());
        category.setDescription(data.description());
        categoryRepository.save(category);
    }
}
