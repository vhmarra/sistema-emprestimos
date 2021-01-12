package br.com.victor.emprestimos.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "perfis")
@Data
public class Perfis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Cliente> cliente;

    @Override
    public String toString() {
        return "Perfis{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
