package br.com.victor.emprestimos.dtos;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class CadastraClienteRequest {

    @ApiParam(required = true)
    private String nome;

    @ApiParam(required = true)
    private String cpf;

    @ApiParam(required = true)
    private String senha;

    @ApiParam(required = true)
    private Double scoreCredito;

}
