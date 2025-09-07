package com.kephas.bookstoreapi.repositories;

import com.kephas.bookstoreapi.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category cat1;
    @BeforeEach
    void setup(){
        cat1 = categoryRepository.save( new Category(
                "Category One", "Desc One"
        ));
    }

    @Test
    @DisplayName("findByName should return a given category")
    void findByName_ShouldReturnCategory() {
        Optional<Category> found = categoryRepository.findByName("Category One");

        assertTrue(found.isPresent());
        assertEquals("Category One", found.get().getName());
        assertEquals("Desc One", found.get().getDescription());
    }


    @Test
    @DisplayName("findByName should return empty when not found")
    void findByName_ShouldReturnEmpty() {
        Optional<Category> found = categoryRepository.findByName("Category Two");

        assertTrue(found.isEmpty());
    }

}