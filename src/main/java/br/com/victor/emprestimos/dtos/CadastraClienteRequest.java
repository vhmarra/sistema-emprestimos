package br.com.victor.emprestimos.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class CadastraClienteRequest {

    @ApiParam(required = true,value = "nome")
    private String nome;

    @JsonProperty(value = "data-nascimento")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @ApiParam(required = true,value = "dd-mm-yyyy",name = "data-nascimento")
    private String dataNascimento;

    @ApiParam(required = true,value = "cpf")
    private String cpf;

    @ApiParam(value = "senha",required = true,type = "string",format = "password")
    private String senha;

    @JsonProperty(value = "score-credito")
    @ApiParam(value = "score-credito")
    private Double scoreCredito;

}
