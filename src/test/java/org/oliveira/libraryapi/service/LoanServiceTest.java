package org.oliveira.libraryapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.oliveira.libraryapi.api.dto.LoanFilterDTO;
import org.oliveira.libraryapi.exception.BusinessException;
import org.oliveira.libraryapi.model.entity.Book;
import org.oliveira.libraryapi.model.entity.Loan;
import org.oliveira.libraryapi.model.repository.LoanRepository;
import org.oliveira.libraryapi.service.impl.LoanServiceImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }


    private Loan createNewLoan() {
        return Loan.builder().book(createNewBook()).customer("Fulano").loanDate(LocalDate.now())
                .build();
    }

    private Book createNewBook() {
        return Book.builder().id(1L).isbn("123").build();
    }

    private LoanFilterDTO createLoanFilterDTO() {
        LoanFilterDTO filterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();
        return filterDTO;
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest(){
        Loan savingLoan = createNewLoan();

        Loan savedLoan = createNewLoan();
        when(repository.existsByBookAndNotReturned(createNewBook())).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve retornar erro ao salvar emprestimo de livro já empresatado")
    public void loanedErrorBookSaveTest(){
        Loan savingLoan = createNewLoan();

        when(repository.existsByBookAndNotReturned(createNewBook())).thenReturn(true);
        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Livro já emprestado");

        Mockito.verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um emprestimo pelo ID")
    public void getLoanDetaisTest(){
        Loan loan = createNewLoan();
        Long id = 1L;
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoanTest(){
        Loan loan = createNewLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findLoanTest() throws Exception{
        Loan loan = createNewLoan();
        loan.setId(1L);

        LoanFilterDTO filterDTO = createLoanFilterDTO();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when(repository.findByBookIsbnOrCustomer(
                Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Loan> result = service.find(filterDTO, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }



}
