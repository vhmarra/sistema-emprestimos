package br.com.victor.emprestimos.domain;

import br.com.victor.emprestimos.enums.StatusEmprestimo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status = StatusEmprestimo.EM_ANALISE;

    @Column(name = "data")
    private LocalDateTime dataSolicitacao;

    @ManyToOne(targetEntity = Cliente.class,optional = false,fetch = FetchType.LAZY)
    private Cliente cliente;

    @Override
    public String toString() {
        return "Emprestimo{" +
                "id=" + id +
                ", valor=" + valor +
                ", status=" + status +
                ", dataSolicitacao=" + dataSolicitacao +
                '}';
    }
}
