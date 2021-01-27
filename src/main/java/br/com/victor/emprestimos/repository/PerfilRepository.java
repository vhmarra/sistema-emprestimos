package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.Perfis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilRepository extends JpaRepository<Perfis,Long> {

}
