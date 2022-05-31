package org.oliveira.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {


    private Long id;



    private Book book;


    private String customer;


    private LocalDate loanDate;


    private Boolean returned;

}
