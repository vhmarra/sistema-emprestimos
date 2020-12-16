package br.com.victor.emprestimos.exceptions;

import lombok.Data;

@Data
public class InvalidCredencialsException extends SecurityException{

    public InvalidCredencialsException(String message){
        super(message);
    }

}
