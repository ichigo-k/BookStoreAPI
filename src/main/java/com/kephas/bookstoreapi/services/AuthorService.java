package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.repositories.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Author getOneAuthor(UUID id){
        return authorRepository.findAuthorById(id);
    }
}
