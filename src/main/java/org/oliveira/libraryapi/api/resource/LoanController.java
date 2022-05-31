package org.oliveira.libraryapi.api.resource;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.oliveira.libraryapi.api.dto.LoanDTO;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.entity.Loan;
import org.oliveira.libraryapi.service.BookService;
import org.oliveira.libraryapi.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO created(@RequestBody LoanDTO dto){
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro não encontrado pelo ISBN" ));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);

        dto.setId(entity.getId());
        dto.setLocalDate(entity.getLoanDate());

        return dto;
    }
}
