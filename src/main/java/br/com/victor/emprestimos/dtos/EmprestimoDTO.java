package br.com.victor.emprestimos.dtos;

import br.com.victor.emprestimos.enums.StatusEmprestimo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
public class EmprestimoDTO {

    private Long id;
    private Double valor;
    private String status;

    @JsonProperty(value = "data-solicitacao")
    private LocalDateTime dataSolicitacao;



}
