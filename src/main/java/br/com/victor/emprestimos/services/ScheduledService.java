package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@EnableScheduling
public class ScheduledService {

    private final TokenRepository tokenRepository;
    private final HistoricoClienteRepository historicoRepository;

    public ScheduledService(TokenRepository tokenRepository, HistoricoClienteRepository historicoRepository) {
        this.tokenRepository = tokenRepository;
        this.historicoRepository = historicoRepository;
    }


    @Scheduled(initialDelay = 1200000L, fixedRate = 1200000L)
    public void removeTokens() {
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
