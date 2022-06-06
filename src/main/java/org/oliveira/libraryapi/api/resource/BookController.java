package org.oliveira.libraryapi.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.oliveira.libraryapi.api.dto.BookDTO;
import org.oliveira.libraryapi.api.dto.LoanDTO;
import org.oliveira.libraryapi.api.exception.ApiErros;
import org.oliveira.libraryapi.exception.BusinessException;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.entity.Loan;
import org.oliveira.libraryapi.service.BookService;
import org.oliveira.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;
import javax.websocket.server.PathParam;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Cadastra um livro")
    public BookDTO create(@RequestBody @Valid BookDTO dto){

        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Busca um livro por ID")
    public BookDTO get(@PathVariable("id") Long id){
        return service
                .getById(id)
                .map (book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Busca livro por parametros")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);

        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Exclui um livro por id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Exclusão do livro com sucesso"),
            @ApiResponse(code = 401, message = "Execução não autorizada"),
            @ApiResponse(code = 403, message = "Erro desconhecido")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping("/{id}")
    @ApiOperation("Altera um livro pelo id")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update (@PathVariable Long id, @RequestBody BookDTO dto){
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());

        service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping("{id}/loans")
    @ApiOperation("Pesquisa um livro emprestado")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable){

        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Page<Loan> result = loanService.getLoansByBook(book, pageable);

        List<LoanDTO> listResult = result.getContent().stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBookDTO(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(listResult, pageable, result.getTotalElements());
    }

}
