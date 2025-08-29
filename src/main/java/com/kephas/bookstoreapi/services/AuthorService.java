package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.repositories.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> getAuthors(){
        return authorRepository.findAll();
    }

    public Author getOneAuthor(UUID id){
        return authorRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("User with "+ id+ " does not exist"));
    }
}