package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteDataDTO;
import br.com.victor.emprestimos.enums.HistoricoClienteEnum;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.repository.TokenRepository;
import br.com.victor.emprestimos.utils.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private HistoricoClienteRepository historicoClienteRepository;

    public ClienteService(ClienteRepository clienteRepository, PerfilRepository perfilRepository,
                          TokenService tokenService, EmprestimoRepository emprestimoRepository,
                          TokenRepository tokenRepository, PerfilService perfilService, HistoricoClienteRepository historicoClienteRepository) {
        this.clienteRepository = clienteRepository;
        this.perfilRepository = perfilRepository;
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.tokenRepository = tokenRepository;
        this.perfilService = perfilService;
        this.historicoClienteRepository = historicoClienteRepository;
    }


    @Transactional
    public void cadastraCliente(CadastraClienteRequest request) throws InvalidInputException {
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

        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoClienteEnum.CADASTROU);
        historicoCliente.setData(LocalDateTime.now());

        historicoCliente2.setCliente(cliente);
        historicoCliente2.setData(LocalDateTime.now());
        historicoCliente2.setHistoricoStatus(HistoricoClienteEnum.GEROU_TOKEN);

        clienteRepository.save(cliente);
        tokenRepository.save(acessToken);
        historicoClienteRepository.save(historicoCliente);
        historicoClienteRepository.save(historicoCliente2);

    }

    @Transactional
    public void autentica(String cpf,String senha) throws InvalidCredencialsException, InvalidInputException {
        Optional<Cliente> cliente = clienteRepository.findByCpfAndSenha(cpf, DigestUtils.sha512Hex(senha));
        HistoricoCliente historicoCliente = new HistoricoCliente();
        if(!cliente.isPresent()){
            throw new InvalidCredencialsException("Dados Invalidos");
        }
        if(tokenService.isTokenValid(cliente.get())){
            throw new InvalidInputException("cliente ja esta autenticado");
        }
        Optional<TokenCliente> token = tokenRepository.findByCliente_Id(cliente.get().getId());

        token.get().setAtivo(true);
        token.get().setDataCriacao(LocalDateTime.now());
        token.get().setToken(tokenService.generateToken(cliente.get()));

        historicoCliente.setCliente(cliente.get());
        historicoCliente.setHistoricoStatus(HistoricoClienteEnum.AUTENTICACAO);
        historicoCliente.setData(LocalDateTime.now());

        tokenRepository.save(token.get());
        historicoClienteRepository.save(historicoCliente);

    }


    @Transactional
    public List<Cliente> findAll(String token) throws InvalidCredencialsException {
        Cliente cliente = tokenService.findClienteByToken(token);
        if(!cliente.getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))){
            throw new InvalidCredencialsException("usuario sem permissao");
        }
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes;

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
