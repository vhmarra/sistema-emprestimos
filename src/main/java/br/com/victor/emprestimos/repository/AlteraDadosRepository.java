package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.AlteraDados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlteraDadosRepository extends JpaRepository<AlteraDados,Long> {

    AlteraDados findByTokenTrocaDados(String token);
    List<AlteraDados> findAllByClienteId(Long id);
}
