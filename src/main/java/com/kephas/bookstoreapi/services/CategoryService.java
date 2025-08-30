package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.CategoryDto;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.CategoryMapper;
import com.kephas.bookstoreapi.repositories.BookRepository;
import com.kephas.bookstoreapi.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;

    public CategoryDto getOneCategory(UUID id){
        Category category = categoryRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException("Category with id: "+id+" does not exist"));
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
            throw new ResourceNotFoundException("Category does not exist");
        }

        if (bookRepository.countBooksByCategory_Id(id) >= 1) {
            List<Book> books = bookRepository.findBooksByCategory_Id(id);

            Category uncategorized = categoryRepository.findByName("Uncategorized")
                    .orElseGet(() -> {
                        Category newCategory = new Category(
                                "Uncategorized",
                                "This category is used for books without a defined category."
                        );
                        return categoryRepository.save(newCategory);
                    });
            books.forEach(book -> book.setCategory(uncategorized));
            bookRepository.saveAll(books);
        }

        categoryRepository.deleteById(id);
    }


    public void updateCategory(UUID id, CategoryDto data) {
        Category category = categoryRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException("Category with id: "+id+" does not exist"));
        category.setName(data.name());
        category.setDescription(data.description());
        categoryRepository.save(category);
    }
}
