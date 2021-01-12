package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteDataDTO;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import br.com.victor.emprestimos.utils.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.mapping.Any;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private ClienteRepository clienteRepository;
    private EmprestimoRepository emprestimoRepository;
    private PerfilRepository perfilRepository;
    private TokenService tokenService;
    private TokenRepository tokenRepository;
    private PerfilService perfilService;

    public ClienteService(ClienteRepository clienteRepository, PerfilRepository perfilRepository,
                          TokenService tokenService, EmprestimoRepository emprestimoRepository,
                          TokenRepository tokenRepository, PerfilService perfilService) {
        this.clienteRepository = clienteRepository;
        this.perfilRepository = perfilRepository;
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.tokenRepository = tokenRepository;
        this.perfilService = perfilService;
    }


    @Transactional
    public void cadastraCliente(CadastraClienteRequest request){
        perfilService.createProfiles();
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
        acessToken.setDataCriacao(LocalDateTime.now());

        //TODO usado somente para validações e testes
        if(cliente.getCpf() == "10809606607"){
            cliente.setPerfis(Arrays.asList(perfilRepository.findById(Constants.ROLE_ADM).get()));
            cliente.setPerfis(Arrays.asList(perfilRepository.findById(Constants.SUPER_ADM).get()));
            acessToken.setAtivo(true);
        }

        clienteRepository.save(cliente);
        tokenRepository.save(acessToken);

    }

    @Transactional
    public void autentica(String cpf,String senha){
        clienteRepository.findByCpfAndSenha(cpf,DigestUtils.sha512Hex(senha)).ifPresent(cliente->{
            Optional<TokenCliente> token = tokenRepository.findByCliente_Id(cliente.getId());
            token.get().setAtivo(true);
            token.get().setDataCriacao(LocalDateTime.now());
            tokenRepository.save(token.get());
        });
    }

    @Transactional
    public List<Cliente> findAll(String token) throws InvalidCredencialsException {
        Cliente cliente = tokenService.findClienteByToken(token);

        if(!cliente.getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))){
            throw new InvalidCredencialsException("usuario sem permissao");
        }
        return clienteRepository.findAll();

    }

    @Transactional
    public ClienteDataDTO getDataIfSuperAdmin(String tokenAdm, String tokenCliente) throws InvalidCredencialsException {
        if(tokenService.findClienteByToken(tokenAdm).getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))){
            Cliente cliente = tokenService.findClienteByToken(tokenCliente);
            ClienteDataDTO dto = new ClienteDataDTO();

            dto.setCpf(cliente.getCpf());
            dto.setNome(cliente.getNome());
            dto.setId(cliente.getId());
            dto.setSenha(cliente.getSenha().replace(cliente.getSenha(),"********************************"));
            dto.setPerfis(Arrays.asList(cliente.getPerfis().toString()));
            dto.setEmprestimos(cliente.getEmprestimos());

            return dto;
        }else{
            throw new InvalidCredencialsException("sem permissao");
        }
    }


}
