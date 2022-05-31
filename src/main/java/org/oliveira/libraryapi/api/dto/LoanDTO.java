package org.oliveira.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;
    private String isbn;
    private String customer;
    private LocalDate localDate;
}
