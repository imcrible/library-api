package org.oliveira.libraryapi.api.exception;

import org.oliveira.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErros {
    private List<String> errors;

    public ApiErros(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(
                error -> this.errors.add(error.getDefaultMessage())
        );
    }

    public ApiErros(BusinessException exception) {
        this.errors = Arrays.asList(exception.getMessage());
    }

    public List<String> getErrors(){
        return errors;
    }
}
