package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.ClienteDataDTO;
import br.com.victor.emprestimos.dtos.EmprestimoDTO;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.dtos.HistoricoClienteDTO;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.ForbiddenException;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.EmprestimoService;
import br.com.victor.emprestimos.services.HistoricoService;
import br.com.victor.emprestimos.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.List;

@RequestMapping("cliente")
@RestController
public class ClienteController {

    private final ClienteService clienteService;
    private final EmprestimoService emprestimoService;
    private final TokenService tokenService;
    private final HistoricoService historicoService;

    public ClienteController(ClienteService clienteService, EmprestimoService emprestimoService, TokenService tokenService, HistoricoService historicoService) {
        this.clienteService = clienteService;
        this.emprestimoService = emprestimoService;
        this.tokenService = tokenService;
        this.historicoService = historicoService;
    }

    @PostMapping("solicita-emprestimo")
    public ResponseEntity<?> solicitaEmprestimo(@RequestHeader String token, @RequestHeader @ModelAttribute EmprestimoRequest valor) throws InvalidTokenException, InvalidInputException, InvalidCredencialsException, MessagingException {
        emprestimoService.solicitaEmprestimo(valor);
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-data")
    public ClienteDataDTO getDataIfSuper(@RequestHeader String tokenCliente, @RequestHeader String token) throws InvalidCredencialsException {
        return clienteService.getDataIfSuperAdmin(tokenCliente);
    }

    @PostMapping("update-emprestimo")
    public ResponseEntity<?> updateEmprestimo(@RequestHeader String token, @RequestHeader Long id, @RequestHeader StatusEmprestimo status) throws InvalidInputException, InvalidCredencialsException, InvalidTokenException {
        emprestimoService.updateEmprestimo(id,status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("emprestimos-by-cliente-token")
    public ResponseEntity<List<EmprestimoDTO>> getAllByClienteToken(@RequestHeader String token) throws InvalidCredencialsException {
        return ResponseEntity.ok(emprestimoService.getAllByToken());
    }

    @PostMapping("remove-all-tokens")
    public ResponseEntity<?> removeTokens(@RequestHeader String token) throws InvalidCredencialsException, InvalidInputException {
        tokenService.removeTokens();
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-all-historico")
    public ResponseEntity<List<HistoricoClienteDTO>> getAllHistorico(@RequestHeader String token) throws ForbiddenException {
        return ResponseEntity.ok(historicoService.getAllHistorico());
    }

}
