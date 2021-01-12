package br.com.victor.emprestimos.exceptions;

import lombok.Data;

@Data
public class InvalidCredencialsException extends Exception{

    public InvalidCredencialsException(String message){
        super(message);
    }

}
