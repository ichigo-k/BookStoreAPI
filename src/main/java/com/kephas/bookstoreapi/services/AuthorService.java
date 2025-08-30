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

@AllArgsConstructor
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    AuthorMapper authorMapper;

    public List<Author> getAuthors(){
        return authorRepository.findAll();
    }

    public Author getOneAuthor(UUID id){
        return authorRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Author with id: "+ id+ " does not exist"));
    }

    public void createAuthor(AuthorDto authorDto) {
        Author author = authorMapper.fromDto(authorDto);
        authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author does not exist");
        }

        if (bookRepository.countBooksByAuthor_Id(id) >= 1) {
            List<Book> books = bookRepository.findBooksByAuthor_Id(id);
            Author unknownAuthor = authorRepository.findAuthorByName("Unknown")
                    .orElseGet(() -> {
                        Author newAuthor = new Author(
                                "Unknown",
                                "Author information is currently unavailable. We're working hard to update it soon"
                        );
                        return authorRepository.save(newAuthor);
                    });
            books.forEach(book -> book.setAuthor(unknownAuthor));
            bookRepository.saveAll(books);
        }

        authorRepository.deleteById(id);
    }


    public void updateAuthor(UUID id, @Valid AuthorDto data) {
        Author author = authorRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Author with id: "+ id+ " does not exist"));
        author.setName(data.name());
        author.setBiography(data.biography());
        authorRepository.save(author);
    }
}