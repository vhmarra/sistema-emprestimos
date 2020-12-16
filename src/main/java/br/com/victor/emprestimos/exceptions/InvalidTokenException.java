package br.com.victor.emprestimos.exceptions;

import lombok.Data;

@Data
public class InvalidTokenException extends Exception{

    public InvalidTokenException(String message){
        super(message);
    }

}
