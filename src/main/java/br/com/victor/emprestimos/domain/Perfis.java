package br.com.victor.emprestimos.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "perfis")
@Data
public class Perfis implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Override
    public String getAuthority() {
        return this.nome;
    }
}
