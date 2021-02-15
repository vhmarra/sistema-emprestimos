package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.Perfis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfis,Long> {
    @Override
    Optional<Perfis> findById(Long id);
}
