package org.oliveira.libraryapi.model.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oliveira.libraryapi.model.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    private Book createdValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("Aventuras").build();
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quanto exisitir um livro na base com o mesmo isbn")
    public void returnTrueWhenIsbnExists(){
        String isbn = "123";
        Book book = createdValidBook();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    @Test
    public void returnFalseWhenIsbnDoesntExist(){
        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }
}
