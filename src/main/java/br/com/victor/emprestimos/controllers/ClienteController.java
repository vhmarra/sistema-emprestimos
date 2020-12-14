package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.EmprestimoDto;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.dtos.LoginClientRequest;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.EmprestimoService;
import br.com.victor.emprestimos.services.TokenService;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.InvalidTransactionException;
import javax.transaction.Transactional;
import java.util.List;

@RequestMapping("cliente")
@RestController
public class ClienteController {

    private final ClienteService service;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final EmprestimoService emprestimoService;

    public ClienteController(ClienteService service, AuthenticationManager authManager, TokenService tokenService, EmprestimoService emprestimoService) {
        this.service = service;
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.emprestimoService = emprestimoService;
    }

    @Transactional
    @PostMapping("cadastro")
    @ApiParam
    public ResponseEntity<?> cadastraCliente(@ModelAttribute CadastraClienteRequest request) throws Exception {
        service.cadastraCliente(request);
        return ResponseEntity.status(201).build();
    }

    @Transactional
    @PostMapping("login")
    public ResponseEntity<?> loginCliente(@RequestBody LoginClientRequest request) throws Exception {
        try{
            service.loginCliente(request);
            return ResponseEntity.ok().build();
        }
        catch (InvalidTransactionException e){
            throw new Exception("ERRO AO LOGAR");
        }

    }

    @Transactional
    @PostMapping("solicita-emprestimo")
    public ResponseEntity<?> solicitaEmprestimo(@RequestHeader String token,@RequestBody EmprestimoRequest request) throws AuthenticationException {
        emprestimoService.solicitaEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @GetMapping("listar-emprestimos")
    public ResponseEntity<List<EmprestimoDto>> listaEmprestimos(@RequestHeader String token) throws AuthenticationException {
        return ResponseEntity.ok(emprestimoService.listarEmprestimos(token));
    }

}
