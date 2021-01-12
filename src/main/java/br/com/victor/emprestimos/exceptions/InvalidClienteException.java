package br.com.victor.emprestimos.exceptions;

import lombok.Data;

@Data
public class InvalidClienteException extends Exception{

    public InvalidClienteException(String message){
        super(message);
    }

}
