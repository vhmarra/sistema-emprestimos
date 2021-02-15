package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.EmailToSent;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.enums.EmailType;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.repository.EmailToSentRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import br.com.victor.emprestimos.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@EnableScheduling
public class ScheduledService {

    private final TokenRepository tokenRepository;
    private final HistoricoClienteRepository historicoRepository;
    private final EmailService emailService;
    private final EmailToSentRepository emailToSentRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final PerfilRepository perfilRepository;

    public ScheduledService(TokenRepository tokenRepository, HistoricoClienteRepository historicoRepository, EmailService emailService, EmailToSentRepository emailToSentRepository, EmprestimoRepository emprestimoRepository, PerfilRepository perfilRepository) {
        this.tokenRepository = tokenRepository;
        this.historicoRepository = historicoRepository;
        this.emailService = emailService;
        this.emailToSentRepository = emailToSentRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.perfilRepository = perfilRepository;
    }

    @Scheduled(initialDelay = 1200000L, fixedRate = 1200000L)
    public void removeTokens() throws Exception {
        log.info("------REMOVENDO TOKENS------");
        List<TokenCliente> tokens = tokenRepository.findAllByAtivo(true);

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

    @Scheduled(initialDelay = 10L, fixedRate = 60000L)
    public void sentEmailsBoasVindas() throws InvalidInputException {
        List<EmailToSent> emailToSents = emailToSentRepository.findAllBySentedAndEmailType(0, EmailType.EMAIL_BOAS_VINDAS);
        if (emailToSents.isEmpty()) {
            //log.warn("NAO HA EMAILS DE BOAS VINDAS PARA ENVIAR");
        } else {
            log.info("Enviando emails de boas vindas...");
            emailToSents.forEach(email -> {
                try {
                    emailService.sendEmail(email.getEmailAddress(), email.getCliente().getNome(),
                            Constants.EMAIL_BOAS_VINDAS_SUBJECT, Constants.EMAIL_BOAS_VINDAS.replace("{}", email.getCliente().getNome().toUpperCase()));
                    email.setSented(1);
                    email.setDateSented(LocalDateTime.now());
                    emailToSentRepository.save(email);
                } catch (Exception e) {
                    log.error("Erro ao enviar emails de boas vindas");
                    e.getMessage();
                }
            });
            log.info("Emails de boas vindas enviados...");
        }
    }

    @Scheduled(initialDelay = 10L, fixedRate = 60000L)
    public void sentEmailsEmprestimoSolicitado() throws InvalidInputException {
        List<EmailToSent> emailToSents = emailToSentRepository.findAllBySentedAndEmailType(0, EmailType.EMAIL_EMPRESTIMO_SOLICITADO);
        if (emailToSents.isEmpty()) {
            //log.warn("NAO HA EMAILS DE SOLICITACAO DE EMPRESTIMOS PARA ENVIAR");
        } else {
            log.info("Enviando emails de solicitacao de emprestimos...");
            emailToSents.forEach(email -> {
                try {
                    emailService.sendEmail(email.getEmailAddress(), email.getCliente().getNome(), Constants.EMAIL_EMPRESTIMO_SOLICITADO_SUBJECT, Constants.EMAIL_EMPRESTIMO_SOLICITADO
                            .replace("cn", email.getCliente().getNome().toUpperCase())
                            .replace("vl", emprestimoRepository.findByClienteIdAndStatus(email.getCliente().getId(), StatusEmprestimo.EM_ANALISE).getValor().toString()));
                    email.setSented(1);
                    email.setDateSented(LocalDateTime.now());
                    emailToSentRepository.save(email);
                } catch (Exception e) {
                    log.error("Erro ao enviar emails de solicitacao de emprestimos");
                    e.printStackTrace();
                }
            });
            log.info("Emails de solicitacao de emprestimos enviados com sucesso");
        }
    }
    @Scheduled(initialDelay = 10L, fixedRate = 6000000000000000000L)
    public void createProfiles() {
        if (perfilRepository.findAll().isEmpty()) {
            log.info("criando perfis");
            List<Perfis> perfis = new ArrayList<>();

            Perfis perfil1 = new Perfis();
            Perfis perfil2 = new Perfis();
            Perfis perfil3 = new Perfis();

            perfil1.setNome("user");
            perfil2.setNome("adm");
            perfil3.setNome("super_adm");

            perfis.add(perfil1);
            perfis.add(perfil2);
            perfis.add(perfil3);

            perfilRepository.saveAll(perfis);
            log.info("perfis criado");
        }else {
            log.info("perfis ja existentes");
        }
    }
}
