package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenCliente,Long> {

    Optional<TokenCliente> findByCliente_Id(Long id);
    Optional<TokenCliente> findByToken(String token);

}