package br.com.victor.emprestimos.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HistoricoClienteDTO {

    private Long id;

    @JsonProperty(value = "id-cliente")
    private Long idCliente;

    @JsonProperty(value = "nome-cliente")
    private String clienteNome;
    private String status;
    private String data;
}
