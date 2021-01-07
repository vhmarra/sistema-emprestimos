package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
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
    public void solicitaEmprestimo(String token, EmprestimoRequest request){
        TokenCliente tokenCliente = tokenRepository.findByToken(token).get();

        if(tokenCliente.getAtivo() == false){
            throw new InputMismatchException("Token Invalido");
        }


        Optional<Cliente> cliente = clienteRepository.findById(tokenCliente.getCliente().getId());
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataSolicitacao(LocalDateTime.now());
        emprestimo.setValor(request.getValor());
        emprestimo.setCliente(cliente.get());
        emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);

        tokenCliente.setAtivo(false);

        emprestimoRepository.save(emprestimo);
        clienteRepository.save(cliente.get());
        tokenRepository.save(tokenCliente);




    }

}
