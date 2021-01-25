package br.com.victor.emprestimos.domain;

import br.com.victor.emprestimos.enums.HistoricoAcoes;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historico_cliente")
public class HistoricoCliente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private HistoricoAcoes historicoStatus;

    @Column(name = "data")
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.EAGER,optional = true)
    @JoinColumn(name = "cliente_id", nullable = true)
    Cliente cliente;

}
