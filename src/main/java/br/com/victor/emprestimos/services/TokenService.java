package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.enums.HistoricoClienteEnum;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@EnableScheduling
@Slf4j
public class TokenService {

    private TokenRepository tokenRepository;
    private HistoricoClienteRepository historicoRepository;

    public TokenService(TokenRepository tokenRepository, HistoricoClienteRepository historicoRepository) {
        this.tokenRepository = tokenRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional
    public String generateToken(Cliente cliente){
        String token = DigestUtils.sha256Hex(cliente.getCpf()+cliente.getNome());
        Random random = new Random();
        return DigestUtils.sha3_256Hex(token+
                random.ints(1,500).limit(60L)
                        .collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append).toString());
    }

    public Boolean isTokenValid(Cliente cliente){
        Optional<TokenCliente> token = tokenRepository.findByCliente_Id(cliente.getId());
        if(token.get().getDataCriacao().isBefore(LocalDateTime.now().minusDays(1))){
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

    @Scheduled(fixedDelay = 43200000L) /*12 horas ou a cada vez que eh iniciado*/
    public void removeTokens(){
        log.info("------REMOVENDO TOKENS------");
        List<TokenCliente> tokens = tokenRepository.findAll();
        tokens.forEach(t->{
            System.out.println(t.getToken());//USADO SOMENTE PARA TESTES
            if(t.getCliente().getId() == 1L){
                t.setAtivo(true);
            }else{
                t.setAtivo(false);
            }
            HistoricoCliente historicoCliente = new HistoricoCliente();
            historicoCliente.setCliente(null);
            historicoCliente.setData(LocalDateTime.now());
            historicoCliente.setHistoricoStatus(HistoricoClienteEnum.REMOVEU_TOKEN);
            tokenRepository.save(t);
            historicoRepository.save(historicoCliente);
        });
        log.info("------TOKENS REMOVIDOS------");
    }

}
