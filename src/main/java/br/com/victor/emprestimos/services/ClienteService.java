package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.LoginClientRequest;
import br.com.victor.emprestimos.dtos.TokenDto;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import org.springframework.http.ResponseEntity;
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

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PerfilRepository perfilRepository;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public ClienteService(ClienteRepository clienteRepository, PerfilRepository perfilRepository, AuthenticationManager authManager, TokenService tokenService) {
        this.clienteRepository = clienteRepository;
        this.perfilRepository = perfilRepository;
        this.authManager = authManager;
        this.tokenService = tokenService;
    }


    public void cadastraCliente(CadastraClienteRequest request) throws Exception {
        Cliente cliente = clienteRepository.findByCpf(request.getCpf()).orElse(null);

        if(cliente != null){
            throw new Exception("Cliente ja existe");
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




}
