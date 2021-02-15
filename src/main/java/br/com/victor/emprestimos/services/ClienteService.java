package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.EmailToSent;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.enums.EmailType;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmailToSentRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import br.com.victor.emprestimos.utils.Constants;
import br.com.victor.emprestimos.utils.TokenTheadService;
import br.com.victor.emprestimos.utils.TokenThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ClienteService extends TokenTheadService {

    private final ClienteRepository clienteRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final PerfilRepository perfilRepository;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final PerfilService perfilService;
    private final HistoricoClienteRepository historicoClienteRepository;
    private final EmailService emailService;
    private final EmailToSentRepository emailToSentRepository;
    private final ValidationService validationService;


    public ClienteService(ClienteRepository clienteRepository, EmprestimoRepository emprestimoRepository,
                          PerfilRepository perfilRepository, TokenService tokenService, TokenRepository tokenRepository,
                          PerfilService perfilService, HistoricoClienteRepository historicoClienteRepository, EmailService emailService, EmailToSentRepository emailToSentRepository, ValidationService validationService) {
        this.clienteRepository = clienteRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.perfilRepository = perfilRepository;
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.perfilService = perfilService;
        this.historicoClienteRepository = historicoClienteRepository;
        this.emailService = emailService;
        this.emailToSentRepository = emailToSentRepository;
        this.validationService = validationService;
    }

    public void cadastraCliente(CadastraClienteRequest request) throws InvalidInputException, MessagingException {
        log.info("cadastrando novo cliente...");

        if (validationService.validaCPF(request.getCpf()).equals(false)) {
            throw new InvalidInputException("CPF INVALIDO");
        }

        //TODO TIRAR POIS EMAIL ESTA SENDO ENVIADO FIXO
        //if(validationService.validaEmail(request.getEmail()).equals(false)){
        //    throw new InvalidInputException("EMAIL INVALIDO");
        // }
        //TODO TIRAR POIS EMAIL ESTA SENDO ENVIADO FIXO


        if (!clienteRepository.findByCpf(request.getCpf()).isEmpty()) {
            throw new InvalidInputException("Cliente ja possui cadastro");
        }

        Cliente cliente = new Cliente();
        TokenCliente acessToken = new TokenCliente();
        HistoricoCliente historicoCliente = new HistoricoCliente();
        HistoricoCliente historicoCliente2 = new HistoricoCliente();

        cliente.setNome(request.getNome());
        cliente.setScoreCredito(request.getScoreCredito());
        cliente.setSenha(DigestUtils.sha512Hex(request.getSenha()));
        cliente.setCpf(StringUtils.deleteAny(request.getCpf(),".-"));

        //TODO TIRAR EMAIL FIXO
        //cliente.setEmail(request.getEmail());
        cliente.setEmail("marravh@gmail.com"); // USADO PARA TESTES
        cliente.setPerfis(Arrays.asList(perfilRepository.findById(Constants.ROLE_USER).get()));

        acessToken.setToken(tokenService.generateToken(cliente));
        acessToken.setCliente(cliente);
        acessToken.setAtivo(false);
        acessToken.setDataCriacao(LocalDateTime.now());

        //TODO usado somente para validações e testes
        if (cliente.getCpf().contains("10809606607")) {
            cliente.setPerfis(perfilRepository.findAll());
            acessToken.setAtivo(false);
        }
        log.info("GERANDO HISTORICO");
        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoAcoes.CADASTROU);
        historicoCliente.setData(LocalDateTime.now());

        historicoCliente2.setCliente(cliente);
        historicoCliente2.setData(LocalDateTime.now());
        historicoCliente2.setHistoricoStatus(HistoricoAcoes.GEROU_TOKEN);
        log.info("HISTORICO GERADO");

        log.info("SALVANDO DADOS");
        Cliente c = clienteRepository.save(cliente);

        EmailToSent emailToSent = new EmailToSent();
        emailToSent.setCliente(c);
        emailToSent.setEmailAddress(c.getEmail());
        emailToSent.setEmailSubject("Cadastro com sucesso");
        emailToSent.setEmailType(EmailType.EMAIL_BOAS_VINDAS);
        emailToSent.setSented(0);
        emailToSent.setMessage(Constants.EMAIL_BOAS_VINDAS.replace("{}", c.getNome().toUpperCase()));
        emailToSent.setDateCreated(LocalDateTime.now());

        tokenRepository.save(acessToken);
        historicoClienteRepository.save(historicoCliente);
        historicoClienteRepository.save(historicoCliente2);
        emailToSentRepository.save(emailToSent);
        log.info("DADOS SALVOS");

    }

    public String autentica(String cpf, String senha) throws InvalidCredencialsException, InvalidInputException {
        log.info("AUTENTICANDO CLIENTE COM CPF {}", cpf);

        if (validationService.validaCPF(cpf).equals(false)) {
            throw new InvalidInputException("CPF INVALIDO");
        }

        Optional<Cliente> cliente = clienteRepository.findByCpfAndSenha(cpf, DigestUtils.sha512Hex(senha));
        HistoricoCliente historicoCliente = new HistoricoCliente();
        if (cliente.isEmpty()) {
            throw new InvalidCredencialsException("Dados Invalidos");
        }
        if (tokenService.isTokenValid(cliente.get())) {
            throw new InvalidInputException("cliente ja esta autenticado");
        }

        Optional<TokenCliente> token = tokenRepository.findByCliente_Id(cliente.get().getId());

        token.get().setAtivo(true);
        token.get().setDataAtualizado(LocalDateTime.now());
        token.get().setToken(tokenService.generateToken(cliente.get()));

        historicoCliente.setCliente(cliente.get());
        historicoCliente.setHistoricoStatus(HistoricoAcoes.AUTENTICACAO);
        historicoCliente.setData(LocalDateTime.now());

        tokenRepository.save(token.get());
        historicoClienteRepository.save(historicoCliente);
        TokenThread.setToken(token.get());
        log.info("CLIENTE AUTENTICADO E TOKEN VALIDADO {} {}", cliente.get().getNome(), getToken());
        return getToken();

    }


}





