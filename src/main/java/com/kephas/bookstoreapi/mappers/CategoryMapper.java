package com.kephas.bookstoreapi.mappers;

import com.kephas.bookstoreapi.dtos.BookDto;
import com.kephas.bookstoreapi.dtos.CategoryDto;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.entities.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CategoryMapper {

    private BookMapper bookMapper;

    public CategoryDto toDto(Category category){
        List<BookDto> books =  category.getBooks() == null ? List.of() : category.getBooks().stream().map(bookMapper::toDto).toList();

        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                books
        );

    }

    public Category fromDto(CategoryDto categoryDto){
        List<Book> books =  categoryDto.books() == null ? List.of() : categoryDto.books().stream().map(bookMapper::fromDto).toList();

        return  new Category(
                categoryDto.id(),
                categoryDto.name(),
                categoryDto.description(),
                books
        );
    }
}
