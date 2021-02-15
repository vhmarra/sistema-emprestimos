package br.com.victor.emprestimos.dtos;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;

@Data
public class CadastraClienteRequest {

    @ApiParam(required = true)
    private String nome;

    @ApiParam(required = true)
    private String cpf;

    @ApiParam(required = false)
    private String email;

    @ApiParam(required = true)
    private String senha;

    @ApiParam(required = true)
    private Double scoreCredito;

}
