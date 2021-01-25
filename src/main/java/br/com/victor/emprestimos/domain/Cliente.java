package br.com.victor.emprestimos.domain;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
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

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "score_credito")
    private Double scoreCredito;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Perfis> perfis;

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", scoreCredito=" + scoreCredito +
                ", perfis=" + perfis +
                '}';
    }

    public String toStringForToken(){
        return "{" +
                "id=" + this.id +
                ", nome='" + this.nome + '\'' +
                ", cpf='" + this.cpf + '\'' +
                ", email='" + this.email + '\'' +
                ", senha='" + "***********************" + '\'' +
                ", scoreCredito=" + this.scoreCredito +
                '}';
    }
}
