package br.com.victor.emprestimos.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmprestimoStatusDTO {

    private Long id;
    private String status;
    private String data;

    @JsonProperty(value = "cliente-name")
    private String clienteName;

}
