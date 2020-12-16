package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.dtos.AlteraEmprestimoRequest;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteAlteraEmprestimoRequest;
import br.com.victor.emprestimos.dtos.LoginClientRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.InvalidTransactionException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private ClienteRepository clienteRepository;
    private EmprestimoRepository emprestimoRepository;
    private PerfilRepository perfilRepository;
    private AuthenticationManager authManager;
    private TokenService tokenService;

    public ClienteService(ClienteRepository clienteRepository, PerfilRepository perfilRepository,
                          AuthenticationManager authManager, TokenService tokenService,EmprestimoRepository emprestimoRepository) {
        this.clienteRepository = clienteRepository;
        this.perfilRepository = perfilRepository;
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
    }


    public void cadastraCliente(CadastraClienteRequest request) throws Exception {
        Cliente cliente = clienteRepository.findByCpf(request.getCpf()).orElse(null);

        if(cliente != null){
            throw new Exception("Cliente ja existe");
        }

        if(request.getScoreCredito() < 0 || request.getScoreCredito() > 1000){
            throw new IllegalArgumentException("Valor invalido para score de credito");
        }

        Cliente client = new Cliente();

        String psw = new BCryptPasswordEncoder().encode(request.getSenha());
        client.setCpf(request.getCpf());
        client.setDataNascimento(LocalDate.parse(request.getDataNascimento(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        client.setSenha(psw);
        client.setNome(request.getNome());
        client.setScoreCredito(request.getScoreCredito());
        client.setPerfis(Arrays.asList(perfilRepository.findById(1L).orElse(null)));

        clienteRepository.save(client);

    }

    public void loginCliente(LoginClientRequest request) throws InvalidTransactionException {
        try {
            Authentication authentication = authManager.authenticate(request.convertToAuth());
            String token = tokenService.geraToken(authentication);
            System.out.println("Bearer "+token);

        }catch (AuthenticationException e){
            throw new InvalidTransactionException("dados invalidos");
        }
    }

    @Transactional
    public void alteraEmprestimo(String token, ClienteAlteraEmprestimoRequest request) throws InvalidTokenException {
        if (!tokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Token Invalido");
        }

        Optional<Cliente> cliente = clienteRepository.findById(tokenService.getClienteId(token));
        List<Emprestimo> emprestimos = emprestimoRepository.findAllByClienteId(cliente.get().getId());

        emprestimos.forEach(e->{
            if(e.getId() == request.getId()){
                if(e.getStatus() == request.getStatus()){
                    throw new InputMismatchException("Request invalido");
                }
                e.setStatus(request.getStatus());
                emprestimoRepository.save(e);
            }
        });

    }


}
