package br.com.victor.emprestimos.exceptions;

import lombok.Data;

@Data
public class InvalidInputException extends Exception{

    public InvalidInputException(String message){
        super(message);
    }

}
