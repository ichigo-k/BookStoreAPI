package com.kephas.bookstoreapi.controllers;


import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.dtos.CategoryDto;
import com.kephas.bookstoreapi.mappers.CategoryMapper;
import com.kephas.bookstoreapi.services.CategoryService;
import com.kephas.bookstoreapi.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryDto>> getCategories() {
        List<CategoryDto> categories = categoryService.getCategories()
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        return ApiResponse.success(null, categories);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        categoryService.createCategory(categoryDto);
        ApiResponse<Object> response = ApiResponse.success(201,"Category created successfully", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDto> getCategory(@PathVariable UUID id){
        CategoryDto categoryDto = categoryService.getOneCategory(id);
        return ApiResponse.success(null, categoryDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CategoryDto> deleteCategory(@PathVariable UUID id){
        categoryService.deleteCategory(id);
        return ApiResponse.success("Category deleted successfully", null);
    }

    @PatchMapping("/{id}")
    public ApiResponse<CategoryDto> updateAuthor(@PathVariable UUID id, @Valid @RequestBody CategoryDto data){
        categoryService.updateCategory(id, data);
        return ApiResponse.success("Category updated successfully", null);
    }

}
