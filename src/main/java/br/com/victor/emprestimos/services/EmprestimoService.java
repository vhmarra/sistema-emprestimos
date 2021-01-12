package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.Historico;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {

    private TokenService tokenService;
    private EmprestimoRepository emprestimoRepository;
    private ClienteRepository clienteRepository;
    private HistoricoRepository historicoRepository;
    private PerfilRepository perfilRepository;
    private TokenRepository tokenRepository;

    public EmprestimoService(TokenService tokenService, EmprestimoRepository emprestimoRepository, ClienteRepository clienteRepository, HistoricoRepository historicoRepository, PerfilRepository perfilRepository, TokenRepository tokenRepository) {
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.clienteRepository = clienteRepository;
        this.historicoRepository = historicoRepository;
        this.perfilRepository = perfilRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void solicitaEmprestimo(String token, EmprestimoRequest request) throws InvalidTokenException, InvalidInputException, InvalidCredencialsException {
        TokenCliente tokenCliente = tokenRepository.findByToken(token).get();

        if(tokenCliente.getAtivo() == false){
            throw new InvalidTokenException("Token Expirado");
        }
        if(!tokenService.isTokenValid(tokenCliente)){
            throw new InvalidTokenException("Token Invalido");
        }

        Optional<Cliente> cliente = Optional.ofNullable(tokenService.findClienteByToken(token));
        List<Emprestimo> emprestimos = emprestimoRepository.findAllByClienteId(cliente.get().getId());

        if(emprestimos.size() > 0){
            if(emprestimos.stream().anyMatch(e->e.getStatus() == StatusEmprestimo.ACEITO)){
                throw new InvalidInputException("Pessoa ja tem emprestimo em andamento");
            }

        }

        if(request.getValor() > 15000.00 || request.getValor() < 0 || cliente.get().getScoreCredito() < 200){
            throw new InvalidInputException("Valores invalidos");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataSolicitacao(LocalDateTime.now());
        emprestimo.setValor(request.getValor());
        emprestimo.setCliente(cliente.get());
        emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);

        Historico historico = new Historico();
        historico.setEmprestimo(emprestimo);
        historico.setDataUpdate(emprestimo.getDataSolicitacao());
        historico.setStatus(emprestimo.getStatus());

        emprestimoRepository.save(emprestimo);
        historicoRepository.save(historico);
        clienteRepository.save(cliente.get());

    }

    @Transactional
    public void updateAllEmprestimos(String token,StatusEmprestimo status) throws InvalidCredencialsException {
        Cliente cliente = tokenService.findClienteByToken(token);
        List<Emprestimo> emprestimos = emprestimoRepository.findAllByClienteId(cliente.getId());

        emprestimos.forEach(e->{
            e.setStatus(status);
            Historico historico = new Historico();
            historico.setEmprestimo(e);
            historico.setDataUpdate(LocalDateTime.now());
            historico.setStatus(status);
            emprestimoRepository.save(e);
            historicoRepository.save(historico);

        });
    }

}
