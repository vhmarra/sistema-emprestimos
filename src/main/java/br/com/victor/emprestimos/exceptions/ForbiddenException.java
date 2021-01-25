package br.com.victor.emprestimos.exceptions;

import lombok.Data;

@Data
public class ForbiddenException extends Exception{

    public ForbiddenException(String message){
        super(message);
    }

}
