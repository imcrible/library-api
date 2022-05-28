package org.oliveira.libraryapi.service.impl;

import org.oliveira.libraryapi.exception.BusinessException;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.repository.BookRepository;
import org.oliveira.libraryapi.service.BookService;
import org.oliveira.libraryapi.util.Constantes;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl (BookRepository repository){
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())){
            //repository.findById(book.getId()).equals(null)
            throw new BusinessException(Constantes.MENSAGEM_ERRO_ISBN);
        }


        return repository.save(book);
    }
}
