package br.com.victor.emprestimos.dtos;

import br.com.victor.emprestimos.enums.StatusEmprestimo;
import lombok.Data;

@Data
public class ClienteAlteraEmprestimoRequest {

    private Long id;
    private StatusEmprestimo status;


}
