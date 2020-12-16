package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {

    List<Emprestimo> findByCliente_Id(Long id);

    List<Emprestimo> findAllByClienteId(Long id);

}
