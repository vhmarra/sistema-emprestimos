package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@EnableScheduling
public class TokenService {

    private TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(Cliente cliente){
        String token = DigestUtils.sha256Hex(cliente.getCpf()+cliente.getNome());
        System.out.println(token);
        return DigestUtils.sha3_256Hex(token);
    }

    @Deprecated
    public Boolean isTokenValid(TokenCliente token){
        if(LocalDateTime.now().isAfter(token.getDataCriacao())){
            TokenCliente t = token;
            t.setAtivo(false);
            tokenRepository.save(t);
            return false;
        }
        return true;
    }

    public Cliente findClienteByToken(String token) throws InvalidCredencialsException {
        TokenCliente tokenCliente = tokenRepository.findByToken(token).orElse(null);
        if(tokenCliente == null){
            throw new InvalidCredencialsException("token invalido");
        }
        return tokenCliente.getCliente();
    }

    @Scheduled(cron= "0 8 * * * *")
    public void removeTokens(){
        List<TokenCliente> tokens = tokenRepository.findAll();
        tokens.forEach(t->{
            t.setAtivo(false);
            tokenRepository.save(t);
        });


    }

}
