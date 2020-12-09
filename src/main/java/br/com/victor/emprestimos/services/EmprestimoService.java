package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class EmprestimoService {

    private final TokenService tokenService;
    private final EmprestimoRepository emprestimoRepository;
    private final ClienteRepository clienteRepository;

    public EmprestimoService(TokenService tokenService, EmprestimoRepository emprestimoRepository, ClienteRepository clienteRepository) {
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.clienteRepository = clienteRepository;
    }

    public void solicitaEmprestimo(String token,EmprestimoRequest request){
        if(tokenService.isTokenValid(token)){
            Cliente cliente = clienteRepository.findById(tokenService.getClienteId(token)).orElse(null);
            if(cliente.getPerfis().stream().anyMatch(perfis -> perfis.getId()==1L)){
                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setDataSolicitacao(LocalDateTime.now());
                emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
                emprestimo.setValor(request.getValor());
                cliente.setEmprestimos(Arrays.asList(emprestimo));
                emprestimoRepository.save(emprestimo);
            }
        }
    }



}
