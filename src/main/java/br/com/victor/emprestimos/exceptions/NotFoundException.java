package br.com.victor.emprestimos.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;


public class NotFoundException extends javassist.NotFoundException {

    public NotFoundException(String message){
        super(message);
    }

}
