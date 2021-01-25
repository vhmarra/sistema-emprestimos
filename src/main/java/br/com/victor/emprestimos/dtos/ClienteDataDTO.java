package br.com.victor.emprestimos.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ClienteDataDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String senha;
    List<EmprestimoDto> emprestimos;
    List<String> perfis;

}
