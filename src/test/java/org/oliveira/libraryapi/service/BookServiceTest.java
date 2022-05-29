package org.oliveira.libraryapi.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.oliveira.libraryapi.exception.BusinessException;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.repository.BookRepository;
import org.oliveira.libraryapi.service.impl.BookServiceImpl;
import org.oliveira.libraryapi.util.Constantes;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    private Book createdValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createdValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(book))
                .thenReturn(Book.builder()
                        .id(1L)
                        .isbn("123")
                        .author("Fulano")
                        .title("As aventuras").build()
                );

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }



    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){

        Book book = createdValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificacoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage(Constantes.MENSAGEM_ERRO_ISBN);

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por ID")
    public void getByIDTest(){
        Long id = 1L;
        Book book =  createdValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }

    @Test
    @DisplayName("Deve retprmar vazio ao obter um livro por ID quando o livro não existir")
    public void bookNotFoundByIDTest(){
        Long id = 1L;


        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro por ID")
    public void bookDeleteByIdTest(){
        Long id = 1L;
        Book book = createdValidBook();
        book.setId(id);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book) );

        Mockito.verify(repository, Mockito.times(1)).deleteById(book.getId());
    }

    @Test
    @DisplayName("Não Deve deletar um livro quando id nulo")
    public void bookDoesntDeleteByIdTest(){
        Long id = 1L;
        Book book = createdValidBook();

        org.junit.jupiter.api.Assertions
                .assertThrows(IllegalArgumentException.class ,() -> service.delete(book));

        Mockito.verify(repository, Mockito.never()).deleteById(book.getId());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void bookUpdateTest(){
        Long id = 1L;
        Book livroAntigo = createdValidBook();
        livroAntigo.setId(id);

        Book livroPraAtualizar = Book.builder().id(id).build();

        Mockito.when(repository.save(livroPraAtualizar)).thenReturn(livroAntigo);


        Book livroAtualizado = service.update(livroPraAtualizar);

        assertThat(livroAtualizado.getId()).isEqualTo(livroPraAtualizar.getId());
        assertThat(livroAtualizado.getIsbn()).isEqualTo(livroPraAtualizar.getIsbn());
        assertThat(livroAtualizado.getTitle()).isEqualTo(livroPraAtualizar.getTitle());
        assertThat(livroAtualizado.getAuthor()).isEqualTo(livroPraAtualizar.getAuthor());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest(){
        Book book = createdValidBook();

        org.junit.jupiter.api.Assertions
                .assertThrows(IllegalArgumentException.class, () -> service.update(book));

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){

        Book book = createdValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);

        Mockito.when(repository.findAll(
                Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);


        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }
}
