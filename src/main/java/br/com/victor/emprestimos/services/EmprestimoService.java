package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.Historico;
import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.dtos.AlteraEmprestimoRequest;
import br.com.victor.emprestimos.dtos.EmprestimoDto;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.exceptions.NotFoundException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmprestimoService {

    private TokenService tokenService;
    private EmprestimoRepository emprestimoRepository;
    private ClienteRepository clienteRepository;
    private HistoricoRepository historicoRepository;
    private PerfilRepository perfilRepository;

    public EmprestimoService(TokenService tokenService, EmprestimoRepository emprestimoRepository, ClienteRepository clienteRepository, HistoricoRepository historicoRepository, PerfilRepository perfilRepository) {
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.clienteRepository = clienteRepository;
        this.historicoRepository = historicoRepository;
        this.perfilRepository = perfilRepository;
    }

    public void solicitaEmprestimo(String token, EmprestimoRequest request) throws InvalidTokenException {
        if (!tokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Token Invalido");
        }

        //TODO adicionar logica comparando score credito com valor do emprestimo
        Optional<Cliente> cliente = clienteRepository.findById(tokenService.getClienteId(token));
        if (cliente.isPresent()) {
            List<Emprestimo> emprestimos = new ArrayList<>();
            Emprestimo emprestimo = new Emprestimo();

            emprestimo.setValor(request.getValor());
            emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
            emprestimo.setDataSolicitacao(LocalDateTime.now());
            emprestimo.setCliente(cliente.get());

            emprestimos.add(emprestimo);

            cliente.get().setEmprestimos(emprestimos);

            Historico historico = new Historico();
            historico.setEmprestimo(emprestimo);
            historico.setStatus(emprestimo.getStatus());
            historico.setDataUpdate(emprestimo.getDataSolicitacao());

            emprestimoRepository.save(emprestimo);
            clienteRepository.save(cliente.get());
            historicoRepository.save(historico);
        }

    }

    @Transactional
    public void deleteEmprestimo(String token, Long id) throws AuthenticationException, InvalidTokenException {
        Optional<Cliente> cliente = clienteRepository.findById(tokenService.getClienteId(token));
        Optional<Perfis> perfil = perfilRepository.findById(3L);
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(id);
        Optional<Historico> historico = historicoRepository.findByEmprestimoId(emprestimo.get().getId());

        if (!tokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Token Invalido");
        }

        //PERFIL 3 DE SUPER MODERADOR
        if (!isSuper(cliente.get())) {
            throw new AuthenticationException("sem autorizacao");
        }

        emprestimoRepository.deleteById(emprestimo.get().getId());
        historicoRepository.deleteById(historico.get().getId());

    }

    public List<EmprestimoDto> listarEmprestimos(String token) throws InvalidTokenException {
        if (!tokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Token Invalido");
        }

        List<Emprestimo> emprestimos = emprestimoRepository.findByCliente_Id(tokenService.getClienteId(token));

        List<EmprestimoDto> response = new ArrayList<>();

        emprestimos.forEach(e -> {
            EmprestimoDto dto = new EmprestimoDto();
            dto.setId(e.getId());
            dto.setStatus(e.getStatus().toString());
            dto.setDataSolicitacao(e.getDataSolicitacao());
            dto.setValor(e.getValor());
            //dto.setNomecliente(e.getCliente().getNome());
            response.add(dto);
        });
        return response;
    }

    @Transactional
    public void alteraEmprestimo(String token, AlteraEmprestimoRequest request) throws InvalidTokenException, InvalidCredencialsException {
        if (!tokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Token Invalido");
        }
        Optional<Cliente> moderador = clienteRepository.findById(tokenService.getClienteId(token));

        if(!isAdm(moderador.get())){
            throw new InvalidCredencialsException("Requisicao feita por um usuario que nao eh moderador");
        }

        Optional<Cliente> cliente = clienteRepository.findByCpf(request.getCpf());

        List<Emprestimo> emprestimos = emprestimoRepository.findAllByClienteId(cliente.get().getId());

        List<Emprestimo> emprestimoAlterar = emprestimos.stream()
                .filter(emprestimo -> emprestimo.getId() == request.getId())
                .collect(Collectors.toList());

        Emprestimo emprestimo = emprestimoAlterar.get(0);
        emprestimo.setStatus(request.getStatus());

        emprestimoRepository.save(emprestimo);
    }

    private boolean isAdm(Cliente entity){
        if(entity.getPerfis().stream().anyMatch(p-> p.getId() == 2L || p.getId() == 3L)){
            return true;
        }
        return false;
    }

    private boolean isSuper(Cliente entity){
        if(entity.getPerfis().stream().anyMatch(p-> p.getId() == 3L)){
            return true;
        }
        return false;
    }
}
