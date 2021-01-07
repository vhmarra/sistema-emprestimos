package br.com.victor.emprestimos.dtos;

import lombok.Data;

@Data
public class CadastraClienteRequest {

    private String nome;
    private String cpf;
    private String senha;
    private Double scoreCredito;

}
