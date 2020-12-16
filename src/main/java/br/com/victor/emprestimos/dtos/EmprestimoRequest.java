package br.com.victor.emprestimos.dtos;

import br.com.victor.emprestimos.enums.StatusEmprestimo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
public class EmprestimoRequest {

    private Double valor;

}
