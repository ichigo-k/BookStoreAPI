package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.entities.Category;
import com.kephas.bookstoreapi.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@AllArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getOneCategory(UUID id){
        return categoryRepository.findCategoryById(id);
    }
}
