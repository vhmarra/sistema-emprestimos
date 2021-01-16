package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.Emprestimo;
import br.com.victor.emprestimos.domain.Historico;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.dtos.EmprestimoDto;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.HistoricoClienteEnum;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.utils.Constants;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {

    private TokenService tokenService;
    private EmprestimoRepository emprestimoRepository;
    private ClienteRepository clienteRepository;
    private HistoricoRepository historicoRepository;
    private PerfilService perfilService;
    private HistoricoClienteRepository historicoClienteRepository;


    public EmprestimoService(TokenService tokenService, EmprestimoRepository emprestimoRepository,
                             ClienteRepository clienteRepository, HistoricoRepository historicoRepository,
                             PerfilService perfilService, HistoricoClienteRepository historicoClienteRepository) {
        this.tokenService = tokenService;
        this.emprestimoRepository = emprestimoRepository;
        this.clienteRepository = clienteRepository;
        this.historicoRepository = historicoRepository;
        this.perfilService = perfilService;
        this.historicoClienteRepository = historicoClienteRepository;
    }

    @Transactional
    public void solicitaEmprestimo(String token, EmprestimoRequest request) throws InvalidTokenException,
            InvalidInputException, InvalidCredencialsException {
        Cliente cliente = tokenService.findClienteByToken(token);


        if(!tokenService.isTokenValid(cliente)){
            throw new InvalidTokenException("Token Invalido");
        }

        if(request.getValor() > 150000.00 || request.getValor() < 0 || cliente.getScoreCredito() < 200){
            throw new InvalidInputException("Valores invalidos");
        }

        if(request.getValor() > 1000 && cliente.getScoreCredito() < 200){ //LOGICA PARA ACEITACAO DO EMPRESTIMO
            throw new InvalidInputException("Cliente com score de credito baixo!");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataSolicitacao(LocalDateTime.now());
        emprestimo.setValor(request.getValor());
        emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
        emprestimo.setCliente(cliente);

        Historico historico = new Historico();
        historico.setEmprestimo(emprestimo);
        historico.setDataUpdate(emprestimo.getDataSolicitacao());
        historico.setStatus(emprestimo.getStatus());

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoClienteEnum.SOLICITOU_EMPRESTIMO);
        historicoCliente.setData(LocalDateTime.now());


        emprestimoRepository.save(emprestimo);
        historicoRepository.save(historico);
        clienteRepository.save(cliente);
        historicoClienteRepository.save(historicoCliente);

    }


    @Transactional
    //TODO mudar logica para aceitacao do emprestimo
    public void updateEmprestimo(String token, Long idEmprestimo, StatusEmprestimo status) throws InvalidCredencialsException,
            InvalidInputException, InvalidTokenException {

        Cliente cliente = tokenService.findClienteByToken(token);
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(idEmprestimo);
        Historico historico = new Historico();

        if(!tokenService.isTokenValid(cliente)){
            throw new InvalidTokenException("Token invalido, se autentique antes");
        }
        if(status == emprestimo.get().getStatus()){
            throw new InvalidInputException("Mesmo Status");
        }
        if(status == StatusEmprestimo.FINALIZADO){
            emprestimo.get().setStatus(status);
            emprestimoRepository.save(emprestimo.get());
            historico.setStatus(status);
            historico.setEmprestimo(emprestimo.get());
            historico.setDataUpdate(LocalDateTime.now());
            historicoRepository.save(historico);

        }
        if(status == StatusEmprestimo.ACEITO && cliente.getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))){
            emprestimo.get().setStatus(status);
            emprestimoRepository.save(emprestimo.get());
            historico.setStatus(status);
            historico.setEmprestimo(emprestimo.get());
            historico.setDataUpdate(LocalDateTime.now());
            historicoRepository.save(historico);
        }
        cliente.setEmprestimos(Arrays.asList(emprestimo.get()));
        clienteRepository.save(cliente);
    }


    public List<EmprestimoDto> getAllByToken(String token) throws InvalidCredencialsException {
        List<Emprestimo> emprestimos = emprestimoRepository.findAllByClienteId(tokenService.findClienteIdByToken(token));
        List<EmprestimoDto> response = new ArrayList<>();

        emprestimos.forEach(e->{
            EmprestimoDto dto = new EmprestimoDto();
            dto.setStatus(e.getStatus().toString());
            dto.setValor(e.getValor());
            dto.setId(e.getId());
            dto.setDataSolicitacao(e.getDataSolicitacao());
            response.add(dto);
        });
        return response;
    }
}
