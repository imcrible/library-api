package org.oliveira.libraryapi.service;


import org.oliveira.libraryapi.model.entity.Book;
import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);
}
