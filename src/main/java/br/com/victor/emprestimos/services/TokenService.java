package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.repository.ClienteRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final ClienteRepository repository;

    public TokenService(ClienteRepository repository) {
        this.repository = repository;
    }


    public String generateToken(Cliente cliente){
        String token = DigestUtils.sha256Hex(cliente.getCpf()+cliente.getNome());
        System.out.println(token);
        return token;
    }

}
