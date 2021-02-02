package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.AlteraDados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlteraDadosRepository extends JpaRepository<AlteraDados,Long> {

    AlteraDados findByTokenTrocaDados(String token);
    List<AlteraDados> findAllByClienteId(Long id);
}
