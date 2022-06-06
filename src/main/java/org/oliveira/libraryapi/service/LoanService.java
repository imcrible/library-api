package org.oliveira.libraryapi.service;

import org.oliveira.libraryapi.api.dto.LoanFilterDTO;
import org.oliveira.libraryapi.api.resource.BookController;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}
