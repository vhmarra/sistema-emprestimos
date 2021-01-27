package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteDataDTO;
import br.com.victor.emprestimos.dtos.EmprestimoDTO;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.repository.ClienteRepository;
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

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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


    public ClienteService(ClienteRepository clienteRepository, EmprestimoRepository emprestimoRepository,
                          PerfilRepository perfilRepository, TokenService tokenService, TokenRepository tokenRepository,
                          PerfilService perfilService, HistoricoClienteRepository historicoClienteRepository, EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.perfilRepository = perfilRepository;
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.perfilService = perfilService;
        this.historicoClienteRepository = historicoClienteRepository;
        this.emailService = emailService;
    }


    public void cadastraCliente(@Valid CadastraClienteRequest request) throws InvalidInputException, MessagingException {
        log.info("cadastrando novo cliente...");

        if(!clienteRepository.findByCpf(request.getCpf()).isEmpty()){
            throw new InvalidInputException("Cliente ja possui cadastro");
        }
        perfilService.createProfiles();
        Cliente cliente = new Cliente();
        TokenCliente acessToken = new TokenCliente();
        HistoricoCliente historicoCliente = new HistoricoCliente();
        HistoricoCliente historicoCliente2 = new HistoricoCliente();

        cliente.setNome(request.getNome());
        cliente.setScoreCredito(request.getScoreCredito());
        cliente.setSenha(DigestUtils.sha512Hex(request.getSenha()));
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setPerfis(Arrays.asList(perfilRepository.findById(Constants.ROLE_USER).get()));

        acessToken.setToken(tokenService.generateToken(cliente));
        acessToken.setCliente(cliente);
        acessToken.setAtivo(false);
        acessToken.setDataCriacao(LocalDateTime.now());

        //TODO usado somente para validações e testes
        if(cliente.getCpf().contains("10809606607")){
            List<Perfis> perfis = new ArrayList<>();
            perfis.add(perfilRepository.findById(Constants.ROLE_ADM).get());
            perfis.add(perfilRepository.findById(Constants.ROLE_USER).get());
            perfis.add(perfilRepository.findById(Constants.SUPER_ADM).get());

            cliente.setPerfis(perfis);
            acessToken.setAtivo(true);
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
        clienteRepository.save(cliente);
        tokenRepository.save(acessToken);
        historicoClienteRepository.save(historicoCliente);
        historicoClienteRepository.save(historicoCliente2);
        String emailBody = "seja bem vindo ao sistema de emprestimos "+cliente.getNome();
        emailService.sendEmail(cliente.getEmail(),null,"Cadastro com sucesso",emailBody);
        log.info("DADOS SALVOS");

    }

    public String autentica(String cpf,String senha) throws InvalidCredencialsException, InvalidInputException {
        log.info("AUTENTICANDO CLIENTE COM CPF {}",cpf);
        Optional<Cliente> cliente = clienteRepository.findByCpfAndSenha(cpf, DigestUtils.sha512Hex(senha));
        HistoricoCliente historicoCliente = new HistoricoCliente();
        if(cliente.isEmpty()){
            throw new InvalidCredencialsException("Dados Invalidos");
        }
        if(tokenService.isTokenValid(cliente.get())){
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
        log.info("CLIENTE AUTENTICADO E TOKEN VALIDADO {} {}",cliente.get().getNome(),getToken());
        return getToken();

    }

    public ClienteDataDTO getDataIfSuperAdmin(String tokenCliente) throws InvalidCredencialsException {
        if(getCliente().getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))){
            Cliente cliente = tokenService.findClienteByToken(tokenCliente);
            List<Emprestimo> emprestimo = emprestimoRepository.findAllByClienteId(cliente.getId());
            ClienteDataDTO dto = new ClienteDataDTO();
            List<EmprestimoDTO> emprestimoDTOS = new ArrayList<>();

            dto.setCpf(cliente.getCpf());
            dto.setNome(cliente.getNome());
            dto.setId(cliente.getId());
            dto.setSenha(cliente.getSenha().replace(cliente.getSenha(),"********************************"));
            dto.setPerfis(Arrays.asList(cliente.getPerfis().toString()));
            emprestimo.forEach(e->{
                EmprestimoDTO edto = new EmprestimoDTO();
                edto.setDataSolicitacao(e.getDataSolicitacao());
                edto.setValor(e.getValor());
                edto.setId(e.getId());
                edto.setStatus(e.getStatus().toString());
                emprestimoDTOS.add(edto);
            });
            dto.setEmprestimos(emprestimoDTOS);

            return dto;
        }else{
            throw new InvalidCredencialsException("SEM PERMISSAO");
        }
    }




}
