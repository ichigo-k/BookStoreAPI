package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.entities.Book;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.mappers.AuthorMapper;
import com.kephas.bookstoreapi.repositories.AuthorRepository;
import com.kephas.bookstoreapi.repositories.BookRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.kephas.bookstoreapi.constants.AuthorMessages.*;

@AllArgsConstructor
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;

    public List<AuthorDto> getAuthors(){

        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    public AuthorDto getOneAuthor(UUID id){
        Author author = authorRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException(String.format(AUTHOR_NOT_FOUND_BY_ID, id)));
        return authorMapper.toDto(author);
    }

    public void createAuthor(AuthorDto authorDto) {
        Author author = authorMapper.fromDto(authorDto);
        authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException(AUTHOR_NOT_FOUND);
        }

        if (bookRepository.countBooksByAuthor_Id(id) >= 1) {
            List<Book> books = bookRepository.findBooksByAuthor_Id(id);
            Author unknownAuthor = authorRepository.findAuthorByName(UNKNOWN_AUTHOR_NAME)
                    .orElseGet(() -> {
                        Author newAuthor = new Author(
                                UNKNOWN_AUTHOR_NAME,
                                UNKNOWN_AUTHOR_BIO
                        );
                        return authorRepository.save(newAuthor);
                    });
            books.forEach(book -> book.setAuthor(unknownAuthor));
            bookRepository.saveAll(books);
        }

        authorRepository.deleteById(id);
    }


    public void updateAuthor(UUID id, @Valid AuthorDto data) {
        Author author = authorRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException(AUTHOR_NOT_FOUND_BY_ID));
        author.setName(data.name());
        author.setBiography(data.biography());
        authorRepository.save(author);
    }
}