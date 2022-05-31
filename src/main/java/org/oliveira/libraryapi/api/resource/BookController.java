package org.oliveira.libraryapi.api.resource;

import org.modelmapper.ModelMapper;
import org.oliveira.libraryapi.api.dto.BookDTO;
import org.oliveira.libraryapi.api.exception.ApiErros;
import org.oliveira.libraryapi.exception.BusinessException;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.service.BookService;
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
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){

//        Book entity = Book.builder()
//                .author(dto.getAuthor())
//                .title(dto.getTitle())
//                .isbn(dto.getIsbn())
//                .build();
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);

//        return BookDTO.builder()
//                .id(entity.getId())
//                .author(entity.getAuthor())
//                .title(entity.getTitle())
//                .isbn(entity.getIsbn())
//                .build();

        return modelMapper.map(entity, BookDTO.class);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO get(@PathVariable("id") Long id){
        return service
                .getById(id)
                .map (book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);

        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update (@PathVariable Long id, @RequestBody BookDTO dto){
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());

        service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }


}
