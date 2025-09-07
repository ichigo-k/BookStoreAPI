package com.kephas.bookstoreapi.repositories;

import com.kephas.bookstoreapi.entities.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("AuthorRepository Custom Methods Tests")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("findAuthorByName should return author when exists")
    void findAuthorByName_ShouldReturnAuthor_WhenExists() {

        Author author = new Author("Kephas", "Test biography");
        authorRepository.save(author);

        Optional<Author> found = authorRepository.findAuthorByName("Kephas");

        assertTrue(found.isPresent());
        assertEquals("Kephas", found.get().getName());
        assertEquals("Test biography", found.get().getBiography());
    }

    @Test
    @DisplayName("findAuthorByName should return empty when author does not exist")
    void findAuthorByName_ShouldReturnEmpty_WhenNotExists() {

        Optional<Author> found = authorRepository.findAuthorByName("Unknown");

        assertTrue(found.isEmpty());
    }
}
