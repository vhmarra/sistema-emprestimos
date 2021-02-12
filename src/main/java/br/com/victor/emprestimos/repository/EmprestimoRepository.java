package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {

    List<Emprestimo> findAllByClienteId(Long id);
    Emprestimo findByClienteIdAndStatus(Long id, StatusEmprestimo status);
    List<Emprestimo> findAllByClienteIdAndStatusNot(Long id,StatusEmprestimo status);

}
