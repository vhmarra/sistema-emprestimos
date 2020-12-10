package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.Historico;
import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.dtos.EmprestimoDto;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmprestimoService {

    private final TokenService tokenService;
    private final EmprestimoRepository emprestimoRepository;
    private final ClienteRepository clienteRepository;
    private final HistoricoRepository historicoRepository;
    private final PerfilRepository perfilRepository;

    public EmprestimoService(TokenService tokenService, EmprestimoRepository emprestimoRepository, ClienteRepository clienteRepository, HistoricoRepository historicoRepository, PerfilRepository perfilRepository) {
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.clienteRepository = clienteRepository;
        this.historicoRepository = historicoRepository;
        this.perfilRepository = perfilRepository;
    }


    public void solicitaEmprestimo(String token, EmprestimoRequest request) throws AuthenticationException {
        if (!tokenService.isTokenValid(token)) {
            throw new AuthenticationException("Dados invalidos");
        }

        //TODO verificar score de credito e validacoes;
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
            historico.setEmprestimo(emprestimos.get(0));
            historico.setStatus(emprestimos.get(0).getStatus());
            historico.setDataUpdate(emprestimos.get(0).getDataSolicitacao());

            emprestimoRepository.save(emprestimos.get(0));
            clienteRepository.save(cliente.get());
            historicoRepository.save(historico);
        }

    }


    public void deleteEmprestimo(String token, Long id) throws AuthenticationException {
        Optional<Cliente> cliente = clienteRepository.findById(tokenService.getClienteId(token));
        Optional<Perfis> perfil = perfilRepository.findById(3L);
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(id);
        Optional<Historico> historico = historicoRepository.findByEmprestimoId(emprestimo.get().getId());

        //PERFIL 3 DE SUPER MODERADOR
        if (!cliente.get().getAuthorities().contains(perfil.get())) {
            throw new AuthenticationException("sem autorizacao");
        }

        emprestimoRepository.deleteById(emprestimo.get().getId());
        historicoRepository.deleteById(historico.get().getId());

    }

    public List<EmprestimoDto> listarEmprestimos(String token) throws AuthenticationException {
        if (!tokenService.isTokenValid(token)) {
            throw new AuthenticationException("Token invalido");
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
    public void alteraEmprestimo(String token, Long id, String cpf, StatusEmprestimo status) throws AuthenticationException {
        if (!tokenService.isTokenValid(token)) {
            throw new AuthenticationException("Token Invalido");
        }

    }


}
