package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.repository.ClienteRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final ClienteRepository repository;

    public AutenticacaoService(ClienteRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Optional<Cliente> cliente = repository.findByCpf(cpf);
        if(cliente.isPresent()){
            return cliente.get();
        }

        throw new UsernameNotFoundException("Dados invalidos para este cliente");
    }
}
