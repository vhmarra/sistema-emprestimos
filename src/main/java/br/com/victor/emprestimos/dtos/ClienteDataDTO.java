package br.com.victor.emprestimos.dtos;

import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.Perfis;
import lombok.Data;

import java.util.List;

@Data
public class ClienteDataDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String senha;
    List<Emprestimo> emprestimos;
    List<String> perfis;

}
