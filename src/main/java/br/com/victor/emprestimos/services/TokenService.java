package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import br.com.victor.emprestimos.utils.Constants;
import br.com.victor.emprestimos.utils.TokenTheadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class TokenService extends TokenTheadService {

    private final TokenRepository tokenRepository;
    private final HistoricoClienteRepository historicoRepository;
    private final PerfilService perfilService;
    private final Environment environment;


    public TokenService(TokenRepository tokenRepository, HistoricoClienteRepository historicoRepository, PerfilService perfilService, Environment environment) {
        this.tokenRepository = tokenRepository;
        this.historicoRepository = historicoRepository;
        this.perfilService = perfilService;
        this.environment = environment;
    }

    public String generateToken(Cliente cliente) {
        log.info("GERANDO TOKEN PARA O CLIENTE {}", cliente.toStringForToken());
        Random random = new Random();
        String token = DigestUtils.sha3_256Hex(DigestUtils.sha256Hex(cliente.getCpf() + cliente.getNome()) +
                random.ints(1, 500).limit(60L)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString());
        log.info("TOKEN GERADO {}", token);
        return token;

    }

    public Boolean isTokenValid(Cliente cliente) {
        Optional<TokenCliente> token = tokenRepository.findByCliente_Id(cliente.getId());
        if (!token.get().getAtivo()) {
            return false;
        }
        return true;
    }

    public Cliente findClienteByToken(String token) throws InvalidCredencialsException {
        TokenCliente tokenCliente = tokenRepository.findByToken(token).orElse(null);
        if (tokenCliente == null) {
            throw new InvalidCredencialsException("token invalido");
        }
        return tokenCliente.getCliente();
    }

    public void removeTokens() throws InvalidCredencialsException {
        if (!getCliente().getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))) {
            throw new InvalidCredencialsException("Sem permissao de super adm");
        }
        log.info("------REMOVENDO TOKENS------");
        List<TokenCliente> tokens = tokenRepository.findAll();
        tokens.forEach(t -> {
            t.setAtivo(false);
            t.setDataAtualizado(LocalDateTime.now());
            log.info("TOKEN {} DESATIVADO", t.getToken());

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setCliente(t.getCliente());
        historicoCliente.setData(LocalDateTime.now());
        historicoCliente.setHistoricoStatus(HistoricoAcoes.REMOVEU_TOKEN);
        tokenRepository.save(t);
        historicoRepository.save(historicoCliente);
        });
        log.info("------TOKENS REMOVIDOS------");
    }

}
