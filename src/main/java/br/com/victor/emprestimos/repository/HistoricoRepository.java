package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico,Long> {
}
