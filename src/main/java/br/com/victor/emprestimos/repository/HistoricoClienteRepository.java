package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.HistoricoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoClienteRepository extends JpaRepository<HistoricoCliente,Long> {
}
