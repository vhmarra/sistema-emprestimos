package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.EmailToSent;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.Historico;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.dtos.EmprestimoDTO;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.EmailType;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmailToSentRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.utils.Constants;
import br.com.victor.emprestimos.utils.TokenTheadService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Slf4j
@Transactional
public class EmprestimoService extends TokenTheadService {

    private final TokenService tokenService;
    private final EmprestimoRepository emprestimoRepository;
    private final ClienteRepository clienteRepository;
    private final HistoricoRepository historicoRepository;
    private final PerfilService perfilService;
    private final HistoricoClienteRepository historicoClienteRepository;
    private final EmailService emailService;
    private final EmailToSentRepository emailToSentRepository;

    public EmprestimoService(TokenService tokenService, EmprestimoRepository emprestimoRepository,
                             ClienteRepository clienteRepository,
                             HistoricoRepository historicoRepository, PerfilService perfilService,
                             HistoricoClienteRepository historicoClienteRepository, EmailService emailService, EmailToSentRepository emailToSentRepository) {
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.clienteRepository = clienteRepository;
        this.historicoRepository = historicoRepository;
        this.perfilService = perfilService;
        this.historicoClienteRepository = historicoClienteRepository;
        this.emailService = emailService;
        this.emailToSentRepository = emailToSentRepository;
    }

    public void solicitaEmprestimo(EmprestimoRequest request) throws InvalidTokenException,
            InvalidInputException, InvalidCredencialsException, MessagingException {
        Cliente cliente = tokenService.findClienteByToken(getToken());
        Emprestimo e = emprestimoRepository.findByClienteId(getClienteId());

        if(e != null){
            throw new InvalidInputException("Cliente ja tem emprestimo ativo");
        }

        if(!tokenService.isTokenValid(cliente)){
            throw new InvalidTokenException("Token Invalido");
        }

        if(request.getValor() > 150000.00 || request.getValor() < 0 || cliente.getScoreCredito() < 200){
            throw new InvalidInputException("Valores invalidos");
        }

        if(request.getValor() > 1000 && cliente.getScoreCredito() < 200){ //LOGICA PARA ACEITACAO DO EMPRESTIMO
            throw new InvalidInputException("Cliente com score de credito baixo!");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataSolicitacao(LocalDateTime.now());
        emprestimo.setValor(request.getValor());
        emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
        emprestimo.setCliente(cliente);

        Historico historico = new Historico();
        historico.setEmprestimo(emprestimo);
        historico.setDataUpdate(emprestimo.getDataSolicitacao());
        historico.setStatus(emprestimo.getStatus());

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoAcoes.SOLICITOU_EMPRESTIMO);
        historicoCliente.setData(LocalDateTime.now());

        EmailToSent emailToSent = new EmailToSent();
        emailToSent.setEmailSubject("Emprestimo solicitado com sucesso");
        emailToSent.setEmailType(EmailType.EMAIL_EMPRESTIMO_SOLICITADO);
        emailToSent.setSented(0);
        emailToSent.setMessage(Constants.EMAIL_EMPRESTIMO_SOLICITADO
                .replace("{}",cliente.getNome().toUpperCase())
                .replace("{}",emprestimo.getValor().toString()));
        emailToSent.setDateCreated(LocalDateTime.now());
        emailToSent.setCliente(cliente);
        emailToSent.setEmailAddress(cliente.getEmail());

        emprestimoRepository.save(emprestimo);
        historicoRepository.save(historico);
        clienteRepository.save(cliente);
        historicoClienteRepository.save(historicoCliente);
        emailToSentRepository.save(emailToSent);

    }

    //TODO mudar logica para aceitacao do emprestimo
    public void updateEmprestimo(Long idEmprestimo, StatusEmprestimo status) throws InvalidCredencialsException,
            InvalidInputException, InvalidTokenException {

        Cliente cliente = tokenService.findClienteByToken(getToken());
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(idEmprestimo);
        Historico historico = new Historico();
        HistoricoCliente historicoCliente = new HistoricoCliente();

        if(!tokenService.isTokenValid(cliente)){
            throw new InvalidTokenException("Token invalido, se autentique antes");
        }
        if(status == emprestimo.get().getStatus()){
            throw new InvalidInputException("Mesmo Status");
        }
        if(status == StatusEmprestimo.FINALIZADO){
            emprestimo.get().setStatus(status);
            emprestimoRepository.save(emprestimo.get());
            historico.setStatus(status);
            historico.setEmprestimo(emprestimo.get());
            historico.setDataUpdate(LocalDateTime.now());
            historicoRepository.save(historico);

        }
        if(status == StatusEmprestimo.ACEITO){
            emprestimo.get().setStatus(status);
            emprestimoRepository.save(emprestimo.get());
            historico.setStatus(status);
            historico.setEmprestimo(emprestimo.get());
            historico.setDataUpdate(LocalDateTime.now());
            historicoRepository.save(historico);
        }
        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoAcoes.ALTEROU_EMPRESTIMO);
        historicoCliente.setData(LocalDateTime.now());
        historicoClienteRepository.save(historicoCliente);
    }

    public List<EmprestimoDTO> getAllByToken() throws InvalidCredencialsException {
        Cliente cliente = tokenService.findClienteByToken(getToken());

        if(cliente == null){
            throw new InvalidCredencialsException("cliente nao encontrado");
        }

        List<Emprestimo> emprestimos = emprestimoRepository.findAllByClienteId(cliente.getId());
        List<EmprestimoDTO> response = new ArrayList<>();

        emprestimos.forEach(e->{
            EmprestimoDTO dto = new EmprestimoDTO();
            dto.setStatus(e.getStatus().toString());
            dto.setValor(e.getValor());
            dto.setId(e.getId());
            dto.setDataSolicitacao(e.getDataSolicitacao());
            response.add(dto);
        });
        return response;
    }

}
