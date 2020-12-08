package br.com.victor.emprestimos.domain;

import lombok.Data;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name = "cliente")
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDateTime dataNascimento;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "senha")
    private String senha;

    @Column(name = "is_logado")
    private boolean isLogado = false;

    @Column(name = "score_credito")
    private Double scoreCredito;

    @OneToMany
    @JoinColumn(name = "id_cliente")
    private List<Emprestimo> emprestimos;



}
