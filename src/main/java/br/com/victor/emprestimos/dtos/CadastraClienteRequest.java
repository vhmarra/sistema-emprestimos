package br.com.victor.emprestimos.dtos;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CadastraClienteRequest {

    private String nome;

    @JsonProperty(value = "data-nascimento")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String dataNascimento;

    private String cpf;

    private String senha;

    @JsonProperty(value = "score-credito")
    private Double scoreCredito;

}
