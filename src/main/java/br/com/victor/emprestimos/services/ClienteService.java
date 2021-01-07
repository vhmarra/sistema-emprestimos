package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import br.com.victor.emprestimos.utils.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@Service
public class ClienteService {

    private ClienteRepository clienteRepository;
    private EmprestimoRepository emprestimoRepository;
    private PerfilRepository perfilRepository;
    private TokenService tokenService;
    private TokenRepository tokenRepository;

    public ClienteService(ClienteRepository clienteRepository, PerfilRepository perfilRepository,
                          TokenService tokenService, EmprestimoRepository emprestimoRepository,
                          TokenRepository tokenRepository) {
        this.clienteRepository = clienteRepository;
        this.perfilRepository = perfilRepository;
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.tokenRepository = tokenRepository;
    }


    @Transactional
    public void cadastraCliente(CadastraClienteRequest request){
        Cliente cliente = new Cliente();
        TokenCliente acessToken = new TokenCliente();

        cliente.setNome(request.getNome());
        cliente.setScoreCredito(request.getScoreCredito());
        cliente.setSenha(DigestUtils.sha512Hex(request.getSenha()));
        cliente.setCpf(request.getCpf());
        cliente.setPerfis(Arrays.asList(perfilRepository.findById(Constants.ROLE_USER).get()));

        acessToken.setToken(tokenService.generateToken(cliente));
        acessToken.setCliente(cliente);
        acessToken.setAtivo(false);
        acessToken.setDataCriacao(LocalDate.now());

        clienteRepository.save(cliente);
        tokenRepository.save(acessToken);

    }

    @Transactional
    public void autentica(String cpf,String senha){
        clienteRepository.findByCpfAndSenha(cpf,DigestUtils.sha512Hex(senha)).ifPresent(cliente->{
            Optional<TokenCliente> token = tokenRepository.findByCliente_Id(cliente.getId());
            token.get().setAtivo(true);
            tokenRepository.save(token.get());
        });
    }



}
