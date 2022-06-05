package org.oliveira.libraryapi.api.resource;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.oliveira.libraryapi.api.dto.BookDTO;
import org.oliveira.libraryapi.api.dto.LoanDTO;
import org.oliveira.libraryapi.api.dto.LoanFilterDTO;
import org.oliveira.libraryapi.api.dto.ReturnedLoanDTO;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.entity.Loan;
import org.oliveira.libraryapi.service.BookService;
import org.oliveira.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto ){
        Loan loan = loanService.getById(id)
                        .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO filterDTO, Pageable pageRequest){
        Page<Loan> result = loanService.find(filterDTO, pageRequest);
        List<LoanDTO> loans = result.getContent().stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTOLent = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBookDTO(bookDTOLent);
                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());

    }
}
