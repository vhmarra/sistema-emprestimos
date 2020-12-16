package br.com.victor.emprestimos.dtos;

import br.com.victor.emprestimos.enums.StatusEmprestimo;
import lombok.Data;

@Data
public class AlteraEmprestimoRequest {

    private Long id;
    private String cpf;
    private StatusEmprestimo status;


}
