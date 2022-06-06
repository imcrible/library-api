package org.oliveira.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;

    @NotEmpty
    private String isbn;

    @NotEmpty
    private String customer;

    private LocalDate localDate;

    @NotEmpty
    private String customerEmail;

    private BookDTO bookDTO;
}
