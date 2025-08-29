package com.kephas.bookstoreapi.repositories;

import com.kephas.bookstoreapi.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findBooksByAuthor_Id(UUID id);

    int countBooksByAuthor_Id(UUID id);
}
