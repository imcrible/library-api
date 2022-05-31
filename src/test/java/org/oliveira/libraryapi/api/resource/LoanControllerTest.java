package org.oliveira.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.oliveira.libraryapi.api.dto.LoanDTO;
import org.oliveira.libraryapi.exception.BusinessException;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.entity.Loan;
import org.oliveira.libraryapi.service.BookService;
import org.oliveira.libraryapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest (controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
    
    @Autowired
    MockMvc mvc;

    static final String LOAN_API = "/api/loans";

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    private LoanDTO createNewLoadDTO() {
        return LoanDTO.builder().isbn("123").customer("Fulano").build();
    }

    private Book createNewBook() {
        return Book.builder().id(1L).isbn("123").build();
    }

    private Loan createNewLoad() {
        return Loan.builder().id(1L).customer("Fulano")
                .book(createNewBook()).loanDate(LocalDate.now()).build();
    }

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception{

        LoanDTO dto = createNewLoadDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(createNewBook()));

        Loan loan = createNewLoad();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class)) ).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente")
    public void invalidIsbnCreateLoanTest() throws Exception{

        LoanDTO dto = createNewLoadDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn(dto.getIsbn())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Livro não encontrado pelo ISBN"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado")
    public void loadedBookErrorOnCreateLoanTest() throws Exception{

        LoanDTO dto = createNewLoadDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = createNewBook();
        BDDMockito.given(bookService.getBookByIsbn(dto.getIsbn())).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Livro já emprestado"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Livro já emprestado"));
    }

}
